package site.siredvin.cloudsolutions.subsystems.kv.sqlite

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dan200.computercraft.api.lua.LuaException
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.kv.KVKeyChangedHook
import site.siredvin.cloudsolutions.subsystems.kv.KVKeyDeletedHook
import site.siredvin.cloudsolutions.subsystems.kv.KeyValueManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.time.Instant
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object KVSQLiteManager : KeyValueManager {
    private var connection: Connection? = null
    private var cleanupFuture: ScheduledFuture<*>? = null
    private val gson = Gson()
    private val mapType = TypeToken.getParameterized(Map::class.java, String::class.java, String::class.java)
    private var keyDeletedHook: KVKeyDeletedHook = KVKeyDeletedHook { it1, it2 -> }
    private var keyChangedHook: KVKeyChangedHook = KVKeyChangedHook {it1, it2, it3 -> }

    private val rawInsertQuery = """
        insert into kv_records_1 (ownerUUID, key, value, expire)
        values (?, ?, ?, ?) on conflict(ownerUUID, key) DO UPDATE SET value = ?, expire = ?
    """.trimIndent()
    private val rawCountQuery = """
        select count(*) from kv_records_1 where ownerUUID = ? and (expire is null or expire <= ?)
    """.trimIndent()
    private val rawIncrQuery = """
        INSERT INTO kv_records_1 (key, value, ownerUUID, expire)
        VALUES (?, ?, ?, ?)
        ON CONFLICT(ownerUUID, key) DO UPDATE SET value = CAST(value AS REAL) + ?
        RETURNING value as REAL;
    """.trimIndent()

    override fun init(server: MinecraftServer, executor: ScheduledExecutorService) {
        val dirPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID)
        if (!Files.isDirectory(dirPath)) Files.createDirectory(dirPath)
        val dbPath = Paths.get(server.getWorldPath(LevelResource.ROOT).toString(), CloudSolutionsCore.MOD_ID, "kv.db").toString()
        connection = org.sqlite.JDBC.createConnection("jdbc:sqlite:$dbPath", Properties())
        connection!!.autoCommit = false
        val statement = connection!!.createStatement()
        statement.use {
            it.execute(
                """
            create table if not exists kv_records_1(ownerUUID varchar(255) not null , key varchar(255) not null , value text not null , expire integer)
            """.trimIndent(),
            )
            it.execute("create index if not exists kv_owner on kv_records_1 (ownerUUID)")
            it.execute("create unique index if not exists kv_owner_key on kv_records_1 (ownerUUID, key)")
        }
        cleanupFuture = executor.scheduleWithFixedDelay({ cleanup() }, 0, 1, TimeUnit.MINUTES)
    }

    override fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {
        cleanupFuture?.cancel(true)
    }

    private fun cleanup() {
        try {
            CloudSolutionsCore.logger.info("Run KV cleanup")
            val now = Instant.now().epochSecond
            connection!!.prepareStatement("delete from kv_records_1 where expire < ? returning ownerUUID, key").use {
                it.setInt(1, now.toInt())
                val result = it.executeQuery()
                while (result.next()) {
                    this.keyDeletedHook.handle(result.getString("ownerUUID"), result.getString("key"))
                }
            }
        } catch (ex: Exception) {
            CloudSolutionsCore.logger.catching(ex)
        }
    }

    fun validateKeyCount(ownerUUID: String, keyToInsert: Int) {
        connection!!.prepareStatement(rawCountQuery).use {
            it.setString(1, ownerUUID)
            it.setLong(2, Instant.now().epochSecond)
            val result = it?.executeQuery()
            val count = result?.getInt(1) ?: 0
            if (count + keyToInsert >= ModConfig.kvStorageKeyLimit) throw LuaException("You have exceeded key limit per player")
        }
    }

    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?) {
        validateKeyCount(ownerUUID, 1)
        connection!!.prepareStatement(rawInsertQuery).use {
            it.setString(1, ownerUUID)
            it.setString(2, key)
            it.setString(3, value)
            it.setString(5, value)
            if (expire != null) {
                it.setInt(4, expire.epochSecond.toInt())
                it.setInt(6, expire.epochSecond.toInt())
            } else {
                it.setNull(4, 0)
                it.setNull(6, 0)
            }
            it.execute()
            this.keyChangedHook.handle(ownerUUID, key, value)
        }
    }

    override fun mput(
        ownerUUID: String,
        values: Map<String, String>,
    ) {
        if (values.isEmpty()) {
            return
        }
        validateKeyCount(ownerUUID, values.count())
        connection!!.prepareStatement(rawInsertQuery).use { query ->
            values.entries.forEach {
                query.setString(1, ownerUUID)
                query.setString(2, it.key)
                query.setString(3, it.value)
                query.setNull(4, 0)
                query.setString(5, it.value)
                query.setNull(6, 0)
                query.addBatch()
            }
            query.executeBatch()
            values.entries.forEach {
                this.keyChangedHook.handle(ownerUUID, it.key, it.value)
            }
        }
    }

    override fun delete(ownerUUID: String, key: String) {
        connection!!.prepareStatement("delete from kv_records_1 where ownerUUID = ? and key = ?").use {
            it.setString(1, ownerUUID)
            it.setString(2, key)
            it.execute()
        }
        this.keyDeletedHook.handle(ownerUUID, key)
    }

    override fun get(ownerUUID: String, key: String): String? {
        return connection!!.prepareStatement("select value from kv_records_1 where ownerUUID = ? and key = ? and (expire is null or expire > ?)").use {
            it.setString(1, ownerUUID)
            it.setString(2, key)
            it.setLong(3, Instant.now().epochSecond)
            val result = it.executeQuery()
            return@use result.getString(1)
        }
    }

    override fun mget(
        ownerUUID: String,
        keys: List<String>,
    ): Map<String, String> {
        if (keys.isEmpty()) {
            return emptyMap()
        }
        val placeholders = java.lang.String.join(",", Collections.nCopies(keys.size, "?"))
        val rawQuery = """SELECT json_group_object(key, value) as result from kv_records_1
    where ownerUUID = ? and(expire is null or expire > ?) and key in ($placeholders)
        """.trimIndent()
        return connection!!.prepareStatement(rawQuery).use {
            it.setString(1, ownerUUID)
            it.setLong(2, Instant.now().epochSecond)
            for (i in 0..<keys.size) {
                it.setString(i + 3, keys.get(i))
            }
            val result = it.executeQuery()
            @Suppress("UNCHECKED_CAST")
            return@use gson.fromJson(result.getString("result"), mapType) as Map<String, String>
        }
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        return connection!!.prepareStatement("select expire from kv_records_1 where ownerUUID = ? and key = ? and (expire is null or expire > ?)").use {
            it.setString(1, ownerUUID)
            it.setString(2, key)
            it.setLong(3, Instant.now().epochSecond)
            val result = it.executeQuery()
            val epoch = result.getInt(1).toLong()
            return@use Instant.ofEpochSecond(epoch)
        }
    }

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?) {
        connection!!.prepareStatement("update kv_records_1 set expire = ? where ownerUUID = ? and key = ? and (expire is null or expire > ?)").use {
            if (expire != null) {
                it.setInt(1, expire.epochSecond.toInt())
            } else {
                it.setNull(1, 0)
            }
            it.setString(2, ownerUUID)
            it.setString(3, key)
            it.setLong(4, Instant.now().epochSecond)
            it.execute()
        }
    }

    override fun list(ownerUUID: String, glob: Optional<String>): List<String> {
        val query = if (glob.isPresent) {
            "select key from kv_records_1 where ownerUUID = ? and(expire is null or expire > ?) and key GLOB ?"
        } else {
            "select key from kv_records_1 where ownerUUID = ? and(expire is null or expire > ?)"
        }
        return connection!!.prepareStatement(query).use {
            it.setString(1, ownerUUID)
            it.setLong(2, Instant.now().epochSecond)
            if (glob.isPresent) {
                it.setString(3, glob.get())
            }
            val result = it.executeQuery()
            val values = mutableListOf<String>()
            if (result != null) {
                while (result.next()) {
                    values.add(result.getString(1))
                }
            }
            return@use values
        }
    }

    override fun incr(ownerUUID: String, key: String, value: Double): Double {
        return connection!!.prepareStatement(rawIncrQuery).use {
            it.setString(1, key)
            it.setString(2, value.toString())
            it.setString(3, ownerUUID)
            it.setNull(4, 0)
            it.setDouble(5, value)
            val result = it.executeQuery()
            if (result != null) {
                result.next()
                val resultValue = result.getDouble(1)
                this.keyChangedHook.handle(ownerUUID, key, resultValue.toString())
                return resultValue
            }
            this.keyChangedHook.handle(ownerUUID, key, value.toString())
            return@use value
        }
    }

    override fun setOnKeyDeletedHook(hook: KVKeyDeletedHook) {
        this.keyDeletedHook = hook
    }

    override fun setOnKeyChangedHook(hook: KVKeyChangedHook) {
        this.keyChangedHook = hook
    }
}
