package site.siredvin.datafortress.subsystems.kv.sqlite

import dan200.computercraft.api.lua.LuaException
import kotlinx.atomicfu.locks.withLock
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.subsystems.kv.KeyValueManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.PreparedStatement
import java.time.Instant
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

object KVSQLiteManager : KeyValueManager {
    private val queryPrepareLock: ReentrantLock = ReentrantLock()
    private var db: Connection? = null
    private var cleanupFuture: ScheduledFuture<*>? = null
    private var cleanupQuery: PreparedStatement? = null
    private var countQuery: PreparedStatement? = null
    private var insertQuery: PreparedStatement? = null
    private var getQuery: PreparedStatement? = null
    private var getExQuery: PreparedStatement? = null
    private var putExQuery: PreparedStatement? = null
    private var listQuery: PreparedStatement? = null

    override fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dirPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), DataFortressCore.MOD_ID)
        if (!Files.isDirectory(dirPath)) Files.createDirectory(dirPath)
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), DataFortressCore.MOD_ID, "kv.db").toString()
        db = org.sqlite.JDBC.createConnection("jdbc:sqlite:$dbPath", Properties())
        db?.autoCommit = true
        val statement = db?.createStatement()
        statement?.execute(
            """
            create table if not exists kv_records_1(ownerUUID varchar(255) not null , key varchar(255) not null , value text not null , expire integer)
            """.trimIndent(),
        )
        statement?.execute("create index if not exists kv_owner on kv_records_1 (ownerUUID)")
        statement?.execute("create unique index if not exists kv_owner_key on kv_records_1 (ownerUUID, key)")
        cleanupQuery = db?.prepareStatement("delete from kv_records_1 where expire < ?")
        countQuery = db?.prepareStatement("select count(*) from kv_records_1 where ownerUUID = ?")
        insertQuery = db?.prepareStatement(
            """
            insert into kv_records_1 (ownerUUID, key, value, expire)
            values (?, ?, ?, ?)
            """.trimIndent(),
        )
        getQuery = db?.prepareStatement("select value from kv_records_1 where ownerUUID = ? and key = ?")
        getExQuery = db?.prepareStatement("select expire from kv_records_1 where ownerUUID = ? and key = ?")
        putExQuery = db?.prepareStatement("update kv_records_1 set expire = ? where ownerUUID = ? and key = ?")
        listQuery = db?.prepareStatement("select key from kv_records_1 where ownerUUID = ?")
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 1, 1, TimeUnit.MINUTES)
    }

    override fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun cleanup() {
        queryPrepareLock.withLock {
            DataFortressCore.LOGGER.info("Run KV cleanup")
            val now = Instant.now().epochSecond
            cleanupQuery?.setInt(1, now.toInt())
            cleanupQuery?.execute()
        }
    }

    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?) {
        queryPrepareLock.withLock {
            countQuery?.setString(1, ownerUUID)
            val result = countQuery?.executeQuery()
            val count = result?.getInt(1) ?: 0
            if (count >= ModConfig.kvStorageKeyLimit) throw LuaException("You have exceeded key limit per player")
            insertQuery?.setString(1, ownerUUID)
            insertQuery?.setString(2, key)
            insertQuery?.setString(3, value)
            if (expire != null) {
                insertQuery?.setInt(4, expire.epochSecond.toInt())
            } else {
                insertQuery?.setNull(4, 0)
            }
            insertQuery?.execute()
        }
    }

    override fun get(ownerUUID: String, key: String): String? {
        return queryPrepareLock.withLock {
            getQuery?.setString(1, ownerUUID)
            getQuery?.setString(2, key)
            val result = getQuery?.executeQuery()
            return@withLock result?.getString(1)
        }
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        return queryPrepareLock.withLock {
            getExQuery?.setString(1, ownerUUID)
            getExQuery?.setString(2, key)
            val result = getExQuery?.executeQuery()
            return@withLock Instant.ofEpochSecond(result?.getInt(1)?.toLong() ?: 0)
        }
    }

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?) {
        queryPrepareLock.withLock {
            if (expire != null) {
                putExQuery?.setInt(1, expire.epochSecond.toInt())
            } else {
                putExQuery?.setNull(1, 0)
            }
            putExQuery?.setString(2, ownerUUID)
            putExQuery?.setString(3, key)
            putExQuery?.execute()
        }
    }

    override fun list(ownerUUID: String): List<String> {
        return queryPrepareLock.withLock {
            listQuery?.setString(1, ownerUUID)
            val result = listQuery?.executeQuery()
            val values = mutableListOf<String>()
            if (result != null) {
                while (result.next()) {
                    values.add(result.getString(1))
                }
            }
            return@withLock values
        }
    }
}
