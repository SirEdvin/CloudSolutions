package site.siredvin.datafortress.subsystems.kv.sqlite

import dan200.computercraft.api.lua.LuaException
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.transaction
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.subsystems.kv.KeyValueManager
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object KVSQLiteManager : KeyValueManager {
    private var db: Database? = null
    private var cleanupFuture: ScheduledFuture<*>? = null

    override fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), DataFortressCore.MOD_ID, "kv.db").toString()
        db = Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC")
        transaction(db=db) {
            SchemaUtils.create(KVRecords)
        }
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 1, 1, TimeUnit.MINUTES)
    }

    override fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun cleanup() {
        transaction(db=db) {
            val now = Instant.now()
            KVRecords.deleteWhere {
                c_expire.less(now)
            }
        }
    }

    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?) {
        transaction(db=db) {
            val keyCount = KVRecords.select {
                KVRecords.c_ownerUUID eq ownerUUID
            }.count()
            if (keyCount >= ModConfig.kvStorageKeyLimit) {
                throw LuaException("You have exceeded key limit per player")
            }
            KVRecords.insert {
                it[c_key] = key
                it[c_ownerUUID] = ownerUUID
                it[c_value] = value
                it[c_expire] = expire
            }
        }
    }

    override fun get(ownerUUID: String, key: String): String? {
        return transaction(db=db) {
            val record = KVRecords.select {
                (KVRecords.c_key eq key) and (KVRecords.c_ownerUUID eq ownerUUID)
            }.singleOrNull() ?: return@transaction null
            return@transaction record[KVRecords.c_value]
        }
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        return transaction(db=db) {
            val record = KVRecords.select {
                (KVRecords.c_key eq key) and (KVRecords.c_ownerUUID eq ownerUUID)
            }.singleOrNull() ?: return@transaction null
            return@transaction record[KVRecords.c_expire]
        }
    }

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?) {
        transaction(db=db) {
            KVRecords.update({
                (KVRecords.c_key eq key) and (KVRecords.c_ownerUUID eq ownerUUID)
            }) {
                it[c_expire] = expire
            }
        }
    }

    override fun list(ownerUUID: String): List<String> {
        return transaction(db=db) {
            return@transaction KVRecords.select {
                KVRecords.c_ownerUUID eq ownerUUID
            }.map { it[KVRecords.c_key] }
        }
    }
}
