package site.siredvin.cloudsolutions.subsystems.kv.sqlite

import dan200.computercraft.api.lua.MethodResult
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.kv.KVKeyChangedHook
import site.siredvin.cloudsolutions.subsystems.kv.KVKeyDeletedHook
import site.siredvin.cloudsolutions.subsystems.kv.KeyValueManager
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

// create table if not exists kv_records_1(ownerUUID varchar(255) not null , key varchar(255) not null , value text not null , expire integer)

object KVRecord : Table("kv_records_1") {
    val ownerUUID = varchar("ownerUUID", 255)
    val key = varchar("key", 255)
    val value = text("value")
    val expire = integer("expire").nullable()

    init {
        uniqueIndex("kv_owner_key`", ownerUUID, key)
    }
}

object KVSQLiteManager : KeyValueManager {
    private var connection: Database? = null
    private var cleanupFuture: ScheduledFuture<*>? = null
    private var keyDeletedHook: KVKeyDeletedHook = KVKeyDeletedHook { it1, it2 -> }
    private var keyChangedHook: KVKeyChangedHook = KVKeyChangedHook { it1, it2, it3 -> }

    private val notExpireCondition: Op<Boolean>
        get() = KVRecord.expire.greater(Instant.now().epochSecond.toInt()).or(KVRecord.expire.isNull())

    override fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dirPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID)
        if (!Files.isDirectory(dirPath)) Files.createDirectory(dirPath)
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID, "kv.db").toString()
        connection = Database.connect("jdbc:sqlite:$dbPath?foreign_keys=on")
        transaction(connection) {
            SchemaUtils.create(KVRecord)
        }
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 0, 1, TimeUnit.MINUTES)
    }

    override fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun cleanup() {
        try {
            CloudSolutionsCore.logger.info("Run KV cleanup")
            val now = Instant.now().epochSecond.toInt()
            transaction(connection) {
                KVRecord.deleteReturning(listOf(KVRecord.ownerUUID, KVRecord.key)) {
                    KVRecord.expire.isNotNull().and(KVRecord.expire.less(now))
                }.forEach {
                    this@KVSQLiteManager.keyDeletedHook.handle(it[KVRecord.ownerUUID], it[KVRecord.key])
                }
            }
        } catch (ex: Exception) {
            CloudSolutionsCore.logger.catching(ex)
        }
    }

    fun tooManyKeys(ownerUUID: String, keyToInsert: Int): Boolean {
        return transaction(connection) {
            val count = KVRecord.selectAll().where(KVRecord.ownerUUID.eq(ownerUUID)).count()
            return@transaction count + keyToInsert >= ModConfig.kvStorageKeyLimit
        }
    }

    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?): MethodResult {
        if (tooManyKeys(ownerUUID, 1)) {
            return MethodResult.of(false, "Too many keys in KV storage")
        }
        val self = this
        return transaction(connection) {
            KVRecord.upsert {
                it[KVRecord.ownerUUID] = ownerUUID
                it[KVRecord.key] = key
                it[KVRecord.value] = value
                it[KVRecord.expire] = expire?.epochSecond?.toInt()
            }
            self.keyChangedHook.handle(ownerUUID, key, value)
            return@transaction MethodResult.of(true)
        }
    }

    override fun mput(
        ownerUUID: String,
        values: Map<String, String>,
    ): MethodResult {
        if (values.isEmpty()) {
            return MethodResult.of(false, "Where is values?")
        }
        if (tooManyKeys(ownerUUID, values.count())) {
            return MethodResult.of(false, "Too many keys already exists")
        }
        val insertedCount = transaction(connection) {
            return@transaction KVRecord.batchUpsert(values.entries) {
                this[KVRecord.ownerUUID] = ownerUUID
                this[KVRecord.key] = it.key
                this[KVRecord.value] = it.value
                this[KVRecord.expire] = null
            }.count()
        }
        values.entries.forEach {
            this.keyChangedHook.handle(ownerUUID, it.key, it.value)
        }
        return MethodResult.of(insertedCount)
    }

    override fun delete(ownerUUID: String, key: String): MethodResult {
        val result = transaction(connection) {
            KVRecord.deleteWhere { KVRecord.ownerUUID.eq(ownerUUID).and(KVRecord.key.eq(key)) }
            return@transaction MethodResult.of(true)
        }
        this.keyDeletedHook.handle(ownerUUID, key)
        return result
    }

    override fun get(ownerUUID: String, key: String): String? {
        return transaction(connection) {
            return@transaction KVRecord.select(KVRecord.value).where(KVRecord.ownerUUID.eq(ownerUUID).and(KVRecord.key.eq(key)).and(notExpireCondition)).singleOrNull()?.get(
                KVRecord.value,
            )
        }
    }

    override fun mget(
        ownerUUID: String,
        keys: List<String>,
    ): Map<String, String> {
        if (keys.isEmpty()) {
            return emptyMap()
        }
        return transaction(connection) {
            val map = mutableMapOf<String, String>()
            KVRecord.select(KVRecord.key, KVRecord.value).where(
                KVRecord.ownerUUID.eq(ownerUUID).and(KVRecord.key.inList(keys)).and(notExpireCondition),
            ).forEach {
                map[it[KVRecord.key]] = it[KVRecord.value]
            }
            return@transaction map
        }
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        return transaction(connection) {
            return@transaction KVRecord.select(KVRecord.expire).where(KVRecord.ownerUUID.eq(ownerUUID).and(KVRecord.key.eq(key)).and(notExpireCondition)).singleOrNull()?.get(
                KVRecord.expire,
            )?.let {
                Instant.ofEpochSecond(it.toLong())
            }
        }
    }

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?): MethodResult {
        return transaction(connection) {
            KVRecord.update({ KVRecord.ownerUUID.eq(ownerUUID).and(KVRecord.key.eq(key)) }) {
                it[KVRecord.expire] = expire?.epochSecond?.toInt()
            }
            return@transaction MethodResult.of(true)
        }
    }

    override fun list(ownerUUID: String, glob: Optional<String>): List<String> {
        return transaction(connection) {
            var baseQuery = KVRecord.select(KVRecord.key).where(KVRecord.ownerUUID.eq(ownerUUID).and(notExpireCondition))
            if (glob.isPresent) {
                baseQuery = baseQuery.where(KVRecord.key.like(glob.get()))
            }
            val list = mutableListOf<String>()
            baseQuery.forEach {
                list.add(it[KVRecord.key])
            }
            return@transaction list
        }
    }

    override fun incr(ownerUUID: String, key: String, value: Double): Double {
        return transaction(connection) {
            return@transaction KVRecord.upsertReturning(KVRecord.ownerUUID, KVRecord.key, returning = listOf(KVRecord.value), onUpdate = {
                it[KVRecord.value] = KVRecord.value.castTo(DoubleColumnType()).plus(value).castTo(
                    VarCharColumnType(),
                )
            }, onUpdateExclude = listOf(KVRecord.key, KVRecord.ownerUUID)) {
                it[KVRecord.key] = key
                it[KVRecord.ownerUUID] = ownerUUID
                it[KVRecord.value] = value.toString()
            }.single()[KVRecord.value].toDouble()
        }
    }

    override fun setOnKeyDeletedHook(hook: KVKeyDeletedHook) {
        this.keyDeletedHook = hook
    }

    override fun setOnKeyChangedHook(hook: KVKeyChangedHook) {
        this.keyChangedHook = hook
    }
}
