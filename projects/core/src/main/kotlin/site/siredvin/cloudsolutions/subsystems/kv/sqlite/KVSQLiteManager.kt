package site.siredvin.cloudsolutions.subsystems.kv.sqlite

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dan200.computercraft.api.lua.LuaException
import kotlinx.atomicfu.locks.withLock
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.kv.KeyValueManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
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
    private var deleteQuery: PreparedStatement? = null
    private var getQuery: PreparedStatement? = null
    private var getExQuery: PreparedStatement? = null
    private var putExQuery: PreparedStatement? = null
    private var listQuery: PreparedStatement? = null
    private var globListQuery: PreparedStatement? = null
    private val gson = Gson()
    private val mapType = TypeToken.getParameterized(Map::class.java, String::class.java, String::class.java)

    private val rawInsertQuery = """
        insert into kv_records_1 (ownerUUID, key, value, expire)
        values (?, ?, ?, ?) on conflict(ownerUUID, key) DO UPDATE SET value = ?, expire = ?
    """.trimIndent()

    override fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dirPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID)
        if (!Files.isDirectory(dirPath)) Files.createDirectory(dirPath)
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID, "kv.db").toString()
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
        countQuery = db?.prepareStatement("select count(*) from kv_records_1 where ownerUUID = ? and (expire is null or expire <= ?)")
        insertQuery = db?.prepareStatement(rawInsertQuery)
        deleteQuery = db?.prepareStatement("delete from kv_records_1 where ownerUUID = ? and key = ?")
        getQuery = db?.prepareStatement("select value from kv_records_1 where ownerUUID = ? and key = ? and (expire is null or expire <= ?)")
        getExQuery = db?.prepareStatement("select expire from kv_records_1 where ownerUUID = ? and key = ? and (expire is null or expire <= ?)")
        putExQuery = db?.prepareStatement("update kv_records_1 set expire = ? where ownerUUID = ? and key = ? and (expire is null or expire <= ?)")
        listQuery = db?.prepareStatement("select key from kv_records_1 where ownerUUID = ? and(expire is null or expire <= ?)")
        globListQuery = db?.prepareStatement("select key from kv_records_1 where ownerUUID = ? and(expire is null or expire <= ?) and key GLOB ?")
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 0, 1, TimeUnit.MINUTES)
        CloudSolutionsCore.logger.info("Result of cleanup future: {}, {}", cleanupFuture?.isDone, cleanupFuture?.isCancelled)
    }

    override fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun cleanup() {
        try {
            queryPrepareLock.withLock {
                CloudSolutionsCore.logger.info("Run KV cleanup")
                val now = Instant.now().epochSecond
                cleanupQuery?.setInt(1, now.toInt())
                cleanupQuery?.execute()
            }
        } catch (ex: Exception) {
            CloudSolutionsCore.logger.catching(ex)
        }
    }

    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?) {
        queryPrepareLock.withLock {
            countQuery?.setString(1, ownerUUID)
            countQuery?.setLong(2, Instant.now().epochSecond)
            val result = countQuery?.executeQuery()
            val count = result?.getInt(1) ?: 0
            if (count >= ModConfig.kvStorageKeyLimit) throw LuaException("You have exceeded key limit per player")
            insertQuery?.setString(1, ownerUUID)
            insertQuery?.setString(2, key)
            insertQuery?.setString(3, value)
            insertQuery?.setString(5, value)
            if (expire != null) {
                insertQuery?.setInt(4, expire.epochSecond.toInt())
                insertQuery?.setInt(6, expire.epochSecond.toInt())
            } else {
                insertQuery?.setNull(4, 0)
                insertQuery?.setNull(6, 0)
            }
            insertQuery?.execute()
        }
    }

    override fun mput(
        ownerUUID: String,
        values: Map<String, String>,
    ) {
        if (values.isEmpty()) {
            return
        }
        queryPrepareLock.withLock {
            val query = db?.prepareStatement(rawInsertQuery) ?: return
            db?.autoCommit = false
            try {
                values.entries.forEach {
                    query.setString(1, ownerUUID)
                    query.setString(2, it.key)
                    query.setString(3, it.value)
                    query.setNull(4, 0)
                    query.addBatch()
                }
                query.executeBatch()
                db?.commit()
            } catch (e: SQLException) {
                db?.rollback()
                throw e
            } finally {
                db?.autoCommit = true
            }
        }
    }

    override fun delete(ownerUUID: String, key: String) {
        queryPrepareLock.withLock {
            deleteQuery?.setString(1, ownerUUID)
            deleteQuery?.setString(2, key)
            deleteQuery?.execute()
        }
    }

    override fun get(ownerUUID: String, key: String): String? {
        return queryPrepareLock.withLock {
            getQuery?.setString(1, ownerUUID)
            getQuery?.setString(2, key)
            getQuery?.setLong(3, Instant.now().epochSecond)
            val result = getQuery?.executeQuery()
            return@withLock result?.getString(1)
        }
    }

    override fun mget(
        ownerUUID: String,
        keys: List<String>,
    ): Map<String, String> {
        if (keys.isEmpty()) {
            return emptyMap()
        }
        return queryPrepareLock.withLock {
            val placeholders = java.lang.String.join(",", Collections.nCopies(keys.size, "?"))
            val rawQuery = """SELECT json_group_object(key, value) as result from kv_records_1
        where ownerUUID = ? and(expire is null or expire <= ?) and key in ($placeholders)
            """.trimIndent()
            val query = db?.prepareStatement(rawQuery) ?: return emptyMap()
            query.setString(1, ownerUUID)
            query.setLong(2, Instant.now().epochSecond)
            for (i in 0..<keys.size) {
                query.setString(i + 3, keys.get(i))
            }
            val result = query.executeQuery()
            @Suppress("UNCHECKED_CAST")
            return@withLock gson.fromJson(result.getString("result"), mapType) as Map<String, String>
        }
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        return queryPrepareLock.withLock {
            getExQuery?.setString(1, ownerUUID)
            getExQuery?.setString(2, key)
            getExQuery?.setLong(3, Instant.now().epochSecond)
            val result = getExQuery?.executeQuery()
            val epoch = result?.getInt(1)?.toLong() ?: return null
            return@withLock Instant.ofEpochSecond(epoch)
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
            putExQuery?.setLong(4, Instant.now().epochSecond)
            putExQuery?.execute()
        }
    }

    override fun list(ownerUUID: String, glob: Optional<String>): List<String> {
        val query = if (glob.isPresent) {
            globListQuery!!
        } else {
            listQuery!!
        }
        return queryPrepareLock.withLock {
            query.setString(1, ownerUUID)
            query.setLong(2, Instant.now().epochSecond)
            if (glob.isPresent) {
                query.setString(3, glob.get())
            }
            val result = query.executeQuery()
            val values = mutableListOf<String>()
            if (result != null) {
                while (result.next()) {
                    values.add(result.getString(1))
                }
            }
            return@withLock values
        }
    }

    override fun incr(ownerUUID: String, key: String, value: Double): Double {
        val preparedQuery = db?.prepareStatement(
            """
            INSERT INTO kv_records_1 (key, value, ownerUUID, expire)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(ownerUUID, key) DO UPDATE SET value = CAST(value AS REAL) + ?
            RETURNING value as REAL;
            """.trimIndent(),
        ) ?: return 0.0
        preparedQuery.setString(1, key)
        preparedQuery.setString(2, value.toString())
        preparedQuery.setString(3, ownerUUID)
        preparedQuery.setNull(4, 0)
        preparedQuery.setDouble(5, value)
        return queryPrepareLock.withLock {
            val result = preparedQuery.executeQuery()
            if (result != null) {
                result.next()
                return@withLock result.getDouble(1)
            }
            return 0.0
        }
    }
}
