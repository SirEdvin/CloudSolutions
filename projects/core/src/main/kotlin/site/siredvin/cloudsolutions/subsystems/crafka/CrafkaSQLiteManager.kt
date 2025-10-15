package site.siredvin.cloudsolutions.subsystems.crafka

import dan200.computercraft.api.lua.MethodResult
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.jetbrains.exposed.v1.core.CustomFunction
import org.jetbrains.exposed.v1.core.IntegerColumnType
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.intLiteral
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.core.wrapAsExpression
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import site.siredvin.cloudsolutions.CloudSolutionsCore
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

data class TopicInformation(val primaryId: Int, val ownerUUID: String, val topic: String, val messageLimit: Int) {
}

object CrafkaTopic: Table("crafka_topic") {
    val id = integer("id").autoIncrement()
    val name  = varchar("name", 255)
    val ownerUUID = varchar("ownerUUID", 255)
    val messageLimit = integer("message_limit")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)

    init {
        uniqueIndex(ownerUUID, name)
    }
}

object CrafkaMessage: Table("crafka_message") {
    val messageID = integer("message_id")
    val topicID = integer("topic_id").references(CrafkaTopic.id)
    val value = text("value")
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(topicID, messageID)
}

object CrafkaSQLiteManager: CrafkaBrokerManager {
    private var connection: Database? = null
    private var cleanupFuture: ScheduledFuture<*>? = null

    override fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dirPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID)
        if (!Files.isDirectory(dirPath)) Files.createDirectory(dirPath)
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID, "crafka_broker.db").toString()
        connection = Database.connect("jdbc:sqlite:$dbPath?foreign_keys=on")
        transaction(connection) {
            SchemaUtils.create(CrafkaTopic, CrafkaMessage)
        }
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 0, 1, TimeUnit.MINUTES)
    }

    private fun cleanup() {
//        try {
//            CloudSolutionsCore.logger.info("Run KV cleanup")
//            val now = Instant.now().epochSecond
//            KVSQLiteManager.connection!!.prepareStatement("delete from kv_records_1 where expire < ? returning ownerUUID, key").use {
//                it.setInt(1, now.toInt())
//                val result = it.executeQuery()
//                while (result.next()) {
//                    this.keyDeletedHook.handle(result.getString("ownerUUID"), result.getString("key"))
//                }
//            }
//        } catch (ex: Exception) {
//            CloudSolutionsCore.logger.catching(ex)
//        }
    }

    override fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun retrieveTopic(ownerUUID: String, topic: String): TopicInformation? {
        return transaction(connection) {
            val row = CrafkaTopic.selectAll().where { CrafkaTopic.ownerUUID.eq(ownerUUID) and (CrafkaTopic.name.eq(topic)) }.singleOrNull() ?: return@transaction null
            return@transaction TopicInformation(
                row[CrafkaTopic.id],
                row[CrafkaTopic.ownerUUID],
                row[CrafkaTopic.name],
                row[CrafkaTopic.messageLimit]
            )
        }
    }

    override fun createTopic(
        ownerUUID: String,
        topic: String,
        messageLimit: Int
    ): MethodResult {
        val existingTopic = retrieveTopic(ownerUUID, topic)
        if (existingTopic != null)
            return MethodResult.of(null, "Such topic already exists")
        return transaction(connection) {
            CrafkaTopic.insert {
                it[name] = topic
                it[this.ownerUUID] = ownerUUID
                it[this.messageLimit] = messageLimit
            }
            return@transaction MethodResult.of(true)
        }
    }

    override fun listTopics(ownerUUID: String): List<String> {
        return transaction(connection) {
            val topics = mutableListOf<String>()
            CrafkaTopic.select(CrafkaTopic.name).where { CrafkaTopic.ownerUUID.eq(ownerUUID) }.forEach {
                topics.add(it[CrafkaTopic.name])
            }
            return@transaction topics
        }
    }

    override fun deleteTopic(
        ownerUUID: String,
        topic: String
    ): MethodResult {
        return transaction(connection) {
            CrafkaTopic.deleteWhere { CrafkaTopic.ownerUUID.eq(ownerUUID).and(CrafkaTopic.name.eq(topic)) }
            return@transaction MethodResult.of(true)
        }
    }

    override fun publish(
        ownerUUID: String,
        topic: String,
        message: String
    ): MethodResult {
        val topic = this.retrieveTopic(ownerUUID, topic) ?: return MethodResult.of(false, "There is no such topic")
        return transaction(connection) {
            CrafkaMessage.insert {
                it[this.topicID] = topic.primaryId
                it[this.value] = message
                it[this.messageID] = CustomFunction(
                    "COALESCE",
                    IntegerColumnType(),
                    wrapAsExpression<Int>(CrafkaMessage.select(CrafkaMessage.messageID.max()).where { CrafkaMessage.topicID eq topic.primaryId }),
                    intLiteral(0)
                ).plus(intLiteral(1))
            }
            return@transaction MethodResult.of(true)
        }
    }

    override fun fetchMessages(
        ownerUUID: String,
        topic: String,
        index: Int
    ): Map<Int, String> {
        val topic = this.retrieveTopic(ownerUUID, topic) ?: return emptyMap()
        return transaction(connection) {
            val map = mutableMapOf<Int, String>()
            CrafkaMessage.select(CrafkaMessage.messageID, CrafkaMessage.value).where {
                CrafkaMessage.topicID.eq(topic.primaryId)
            }.forEach {
                map[it[CrafkaMessage.messageID]] = it[CrafkaMessage.value]
            }
            return@transaction map
        }
    }
}