package site.siredvin.cloudsolutions.subsystems.crafka

import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.shared.computer.core.ServerContext
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.intLiteral
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.wrapAsExpression
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object CrafkaSQLiteManager {
    private var connection: Database? = null
    private var cleanupFuture: ScheduledFuture<*>? = null

    const val CRAFKA_BROKER_MESSAGE_EVENT = "crafka_broker_message"

    fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dirPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID)
        if (!Files.isDirectory(dirPath)) Files.createDirectory(dirPath)
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID, "crafka_broker.db").toString()
        connection = Database.connect("jdbc:sqlite:$dbPath?foreign_keys=on")
        transaction(connection) {
            SchemaUtils.create(CrafkaTopic, CrafkaMessage, CrafkaSubscription)
        }
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 0, 1, TimeUnit.MINUTES)
    }

    fun revalidateSubscription(subscriptionID: Int, previousCursor: Int, attempt: Int) {
        try {
            if (attempt >= ModConfig.crafkaMaxResendSteps) {
                transaction(connection) {
                    CrafkaSubscription.deleteWhere { CrafkaSubscription.id.eq(subscriptionID) }
                }
            } else {
                transaction(connection) {
                    val sub = CrafkaSubscription.join(CrafkaTopic, JoinType.RIGHT).select(
                        CrafkaSubscription.id,
                        CrafkaSubscription.cursor,
                        CrafkaSubscription.topicID,
                        CrafkaSubscription.fragile,
                        CrafkaSubscription.autoCursor,
                        CrafkaSubscription.computerID,
                        CrafkaTopic.ownerUUID,
                        CrafkaTopic.name,
                    ).where(CrafkaSubscription.id.eq(subscriptionID)).singleOrNull() ?: return@transaction
                    val currentCursor = sub[CrafkaSubscription.cursor]
                    val message = CrafkaMessage.selectAll().where(
                        CrafkaMessage.topicID.eq(sub[CrafkaSubscription.topicID]).and(
                            CrafkaMessage.messageID.greater(sub[CrafkaSubscription.cursor]),
                        ),
                    ).orderBy(CrafkaMessage.messageID).limit(1).singleOrNull() ?: return@transaction
                    val manualCursorUpdateRequired = publishMessageToSubscription(
                        sub[CrafkaTopic.name],
                        message[CrafkaMessage.messageID],
                        message[CrafkaMessage.value],
                        sub,
                    )
                    val isNeededRescheduling = if (manualCursorUpdateRequired) {
                        true
                    } else {
                        CrafkaMessage.selectAll().where(
                            CrafkaMessage.topicID.eq(sub[CrafkaSubscription.topicID]).and(
                                CrafkaMessage.messageID.greater(message[CrafkaMessage.messageID]),
                            ),
                        ).count() > 0
                    }
                    if (isNeededRescheduling) {
                        val previousCursorNewValue = if (manualCursorUpdateRequired) currentCursor else message[CrafkaMessage.messageID]
                        val attemptNewValue = if (!manualCursorUpdateRequired || currentCursor != previousCursor) 1 else attempt + 1
                        SubsystemManager.executorService.schedule({
                            revalidateSubscription(subscriptionID, previousCursorNewValue, attemptNewValue)
                        }, ModConfig.crafkaCursorRevalidationDelay * attemptNewValue, TimeUnit.MILLISECONDS)
                    }
                }
            }
        } catch (e: Exception) {
            CloudSolutionsCore.logger.error("Error when revalidating subscription: $e")
        }
    }

    fun publishMessageToSubscription(topic: String, messageID: Int, message: String, record: ResultRow): Boolean {
        val serverContext = ServerContext.get(PlatformToolkit.get().minecraftServer)
        val registry = serverContext.registry()
        val computerID = record[CrafkaSubscription.computerID]
        val computers = registry.computers.filter { it.id == computerID }
        if (computers.isEmpty()) {
            if (record[CrafkaSubscription.fragile]) {
                CrafkaSubscription.deleteWhere { CrafkaSubscription.id.eq(record[CrafkaSubscription.id]) }
            }
        } else {
            @Suppress("TYPE_INTERSECTION_AS_REIFIED_WARNING")
            computers.forEach {
                it.queueEvent(CRAFKA_BROKER_MESSAGE_EVENT, arrayOf(topic, messageID, message))
            }
            if (record[CrafkaSubscription.autoCursor]) {
                CrafkaSubscription.update({
                    CrafkaSubscription.id.eq(record[CrafkaSubscription.id]).and(CrafkaSubscription.cursor.less(messageID))
                }) {
                    it[CrafkaSubscription.cursor] = messageID
                }
                return false
            }
            return true
        }
        return false
    }

    fun redistributeMessage(topic: TopicInformation, messageID: Int, message: String) {
        try {
            transaction(connection) {
                CrafkaSubscription.selectAll().where(
                    CrafkaSubscription.topicID.eq(topic.primaryId).and(
                        CrafkaSubscription.cursor.less(messageID),
                    ),
                ).forEach {
                    val needManualCursorUpdate = publishMessageToSubscription(topic.topic, messageID, message, it)
                    if (needManualCursorUpdate) {
                        SubsystemManager.executorService.schedule({
                            revalidateSubscription(it[CrafkaSubscription.id], it[CrafkaSubscription.cursor], 1)
                        }, ModConfig.crafkaCursorRevalidationDelay, TimeUnit.MILLISECONDS)
                    }
                }
            }
        } catch (e: Exception) {
            CloudSolutionsCore.logger.error("Exception when trying to redistribute message: $e")
        }
    }

    fun subscribe(ownerUUID: String, topic: String, computerID: Int, cursor: Int, fragile: Boolean, autoCursor: Boolean): MethodResult {
        val topic = retrieveTopic(ownerUUID, topic) ?: return MethodResult.of(false, "There is no such topic")
        val subID = transaction(connection) {
            val sub = CrafkaSubscription.insertReturning(listOf(CrafkaSubscription.id)) {
                it[CrafkaSubscription.cursor] = cursor
                it[CrafkaSubscription.fragile] = fragile
                it[CrafkaSubscription.autoCursor] = autoCursor
                it[CrafkaSubscription.topicID] = topic.primaryId
                it[CrafkaSubscription.computerID] = computerID
            }.singleOrNull() ?: return@transaction null
            return@transaction sub[CrafkaSubscription.id]
        } ?: return MethodResult.of(false, "Cannot create subscription")
        SubsystemManager.executorService.submit {
            revalidateSubscription(subID, cursor, 1)
        }
        return MethodResult.of(true, subID)
    }

    fun unsubscribe(ownerUUID: String, topic: String, computerID: Int): MethodResult {
        val topic = retrieveTopic(ownerUUID, topic) ?: return MethodResult.of(false, "There is no such topic")
        return transaction(connection) {
            CrafkaSubscription.deleteWhere { CrafkaSubscription.topicID.eq(topic.primaryId).and(CrafkaSubscription.computerID.eq(computerID)) }
            return@transaction MethodResult.of(true)
        }
    }

    fun onComputerDetach(ownerUUID: String, computerID: Int) {
        transaction(connection) {
            CrafkaSubscription.deleteWhere {
                CrafkaSubscription.computerID.eq(computerID) and
                    CrafkaSubscription.fragile.eq(true) and
                    CrafkaSubscription.topicID.inSubQuery(
                        CrafkaTopic.select(CrafkaTopic.id)
                            .where { CrafkaTopic.ownerUUID eq ownerUUID },
                    )
            }
        }
    }

    fun onComputerAttach(ownerUUID: String, computerID: Int) {
        transaction(connection) {
            val maxMessageID = CrafkaMessage.messageID.max().alias("max_message_id")
            val maxMessageIDByTopic = CrafkaMessage.select(
                CrafkaMessage.topicID,
                maxMessageID,
            ).groupBy(CrafkaMessage.topicID).alias("max_message_ids")
            CrafkaSubscription.innerJoin(CrafkaTopic)
                .join(maxMessageIDByTopic, JoinType.RIGHT, CrafkaSubscription.topicID, maxMessageIDByTopic[CrafkaMessage.topicID])
                .select(CrafkaSubscription.id, CrafkaSubscription.cursor)
                .where(
                    (CrafkaTopic.ownerUUID eq ownerUUID) and
                        (CrafkaSubscription.computerID eq computerID) and
                        (CrafkaSubscription.cursor.less(maxMessageIDByTopic[maxMessageID])),
                ).forEach {
                    SubsystemManager.executorService.schedule({
                        revalidateSubscription(it[CrafkaSubscription.id], it[CrafkaSubscription.cursor], 1)
                    }, ModConfig.crafkaCursorRevalidationDelay, TimeUnit.MILLISECONDS)
                }
        }
    }

    fun getSubscription(ownerUUID: String, topic: String, computerID: Int): MethodResult {
        val topic = retrieveTopic(ownerUUID, topic) ?: return MethodResult.of(null, "There is no such topic")
        return transaction(connection) {
            val sub = CrafkaSubscription.selectAll().where(
                CrafkaSubscription.topicID.eq(topic.primaryId).and(CrafkaSubscription.computerID.eq(computerID)),
            ).singleOrNull() ?: return@transaction MethodResult.of(null, "There is no such subscription")
            return@transaction MethodResult.of(
                mapOf(
                    "cursor" to sub[CrafkaSubscription.cursor],
                    "autoCursor" to sub[CrafkaSubscription.autoCursor],
                    "fragile" to sub[CrafkaSubscription.fragile],
                ),
            )
        }
    }

    fun setCursor(ownerUUID: String, topic: String, computerID: Int, cursor: Int): MethodResult {
        val topic = retrieveTopic(ownerUUID, topic) ?: return MethodResult.of(false, "There is no such topic")
        return transaction(connection) {
            CrafkaSubscription.update({
                CrafkaSubscription.topicID.eq(topic.primaryId).and(CrafkaSubscription.computerID.eq(computerID)).and(
                    CrafkaSubscription.cursor.less(cursor),
                )
            }) {
                it[CrafkaSubscription.cursor] = cursor
            }
            return@transaction MethodResult.of(true)
        }
    }

    private fun cleanup() {
        try {
            CloudSolutionsCore.logger.info("Run Crafka broker cleanup")
            transaction(connection) {
                val countColumn = CrafkaMessage.messageID.count().alias("message_count")
                CrafkaMessage.innerJoin(CrafkaTopic)
                    .select(CrafkaTopic.id, countColumn, CrafkaTopic.messageLimit)
                    .groupBy(CrafkaTopic.id, CrafkaTopic.messageLimit)
                    .having {
                        CrafkaMessage.messageID.count().greater(CrafkaTopic.messageLimit.castTo(LongColumnType()))
                    }
                    .forEach { res ->
                        val subquery = CrafkaMessage.select(CrafkaMessage.messageID)
                            .where(CrafkaMessage.topicID.eq(res[CrafkaTopic.id]))
                            .orderBy(CrafkaMessage.messageID).limit((res[countColumn] - res[CrafkaTopic.messageLimit]).toInt())
                        CrafkaMessage.deleteWhere {
                            CrafkaMessage.messageID.inSubQuery(subquery).and(
                                CrafkaMessage.topicID.eq(res[CrafkaTopic.id]),
                            )
                        }
                    }
            }
        } catch (ex: Exception) {
            CloudSolutionsCore.logger.catching(ex)
        }
    }

    fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun retrieveTopic(ownerUUID: String, topic: String): TopicInformation? {
        return transaction(connection) {
            val row = CrafkaTopic.selectAll().where { CrafkaTopic.ownerUUID.eq(ownerUUID) and (CrafkaTopic.name.eq(topic)) }.singleOrNull() ?: return@transaction null
            return@transaction TopicInformation(
                row[CrafkaTopic.id],
                row[CrafkaTopic.ownerUUID],
                row[CrafkaTopic.name],
                row[CrafkaTopic.messageLimit],
            )
        }
    }

    fun topicCount(ownerUUID: String): Int {
        return transaction(connection) {
            return@transaction CrafkaTopic.selectAll().where(
                CrafkaTopic.ownerUUID.eq(ownerUUID),
            ).count().toInt()
        }
    }

    fun createTopic(
        ownerUUID: String,
        topic: String,
        messageLimit: Int,
    ): MethodResult {
        val existingTopic = retrieveTopic(ownerUUID, topic)
        if (existingTopic != null) {
            return MethodResult.of(null, "Such topic already exists")
        }
        return transaction(connection) {
            CrafkaTopic.insert {
                it[name] = topic
                it[this.ownerUUID] = ownerUUID
                it[this.messageLimit] = messageLimit
            }
            return@transaction MethodResult.of(true)
        }
    }

    fun listTopics(ownerUUID: String): List<String> {
        return transaction(connection) {
            val topics = mutableListOf<String>()
            CrafkaTopic.select(CrafkaTopic.name).where { CrafkaTopic.ownerUUID.eq(ownerUUID) }.forEach {
                topics.add(it[CrafkaTopic.name])
            }
            return@transaction topics
        }
    }

    fun deleteTopic(
        ownerUUID: String,
        topic: String,
    ): MethodResult {
        return transaction(connection) {
            CrafkaTopic.deleteWhere { CrafkaTopic.ownerUUID.eq(ownerUUID).and(CrafkaTopic.name.eq(topic)) }
            return@transaction MethodResult.of(true)
        }
    }

    fun publish(
        ownerUUID: String,
        topic: String,
        message: String,
    ): MethodResult {
        val topic = this.retrieveTopic(ownerUUID, topic) ?: return MethodResult.of(false, "There is no such topic")
        val messageID = transaction(connection) {
            return@transaction CrafkaMessage.insertReturning(listOf(CrafkaMessage.messageID)) {
                it[this.topicID] = topic.primaryId
                it[this.value] = message
                it[this.messageID] = CustomFunction(
                    "COALESCE",
                    IntegerColumnType(),
                    wrapAsExpression<Int>(
                        CrafkaMessage.select(CrafkaMessage.messageID.max())
                            .where { CrafkaMessage.topicID eq topic.primaryId },
                    ),
                    intLiteral(0),
                ).plus(intLiteral(1))
            }.single()[CrafkaMessage.messageID]
        }
        redistributeMessage(topic, messageID, message)
        return MethodResult.of(true)
    }

    fun fetchMessages(
        ownerUUID: String,
        topic: String,
        index: Int,
    ): Map<Int, String> {
        val topic = this.retrieveTopic(ownerUUID, topic) ?: return emptyMap()
        return transaction(connection) {
            val map = mutableMapOf<Int, String>()
            CrafkaMessage.select(CrafkaMessage.messageID, CrafkaMessage.value).where {
                CrafkaMessage.topicID.eq(topic.primaryId).and(CrafkaMessage.messageID.greaterEq(index))
            }.forEach {
                map[it[CrafkaMessage.messageID]] = it[CrafkaMessage.value]
            }
            return@transaction map
        }
    }

    fun describeTopic(ownerUUID: String, topic: String): Map<String, Any> {
        val topic = this.retrieveTopic(ownerUUID, topic) ?: return emptyMap()
        val data = mutableMapOf<String, Any>()
        data["messageLimit"] = topic.messageLimit
        return transaction(connection) {
            val min = CrafkaMessage.messageID.min()
            val max = CrafkaMessage.messageID.max()
            val result = CrafkaMessage.select(min, max).where(
                CrafkaMessage.topicID.eq(topic.primaryId),
            ).single()
            data["firstMessage"] = result[min] ?: 0
            data["lastMessage"] = result[max] ?: 0
            return@transaction data
        }
    }
}
