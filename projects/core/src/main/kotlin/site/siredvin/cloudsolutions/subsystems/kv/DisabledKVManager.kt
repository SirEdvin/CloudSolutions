package site.siredvin.cloudsolutions.subsystems.kv

import dan200.computercraft.api.lua.LuaException
import java.time.Instant
import java.util.Optional

object DisabledKVManager : KeyValueManager {
    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?): Unit = throw LuaException("KV storage is disabled in server configuration")
    override fun mput(
        ownerUUID: String,
        values: Map<String, String>,
    ): Unit = throw LuaException("KV storage is disabled in server configuration")

    override fun delete(ownerUUID: String, key: String): Unit = throw LuaException("KV storage is disabled in server configuration")

    override fun get(ownerUUID: String, key: String): String? = throw LuaException("KV storage is disabled in server configuration")
    override fun mget(
        ownerUUID: String,
        keys: List<String>,
    ): Map<String, String> = throw LuaException("KV storage is disabled in server configuration")

    override fun getExpire(ownerUUID: String, key: String): Instant? = throw LuaException("KV storage is disabled in server configuration")

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?): Unit = throw LuaException("KV storage is disabled in server configuration")

    override fun list(ownerUUID: String, glob: Optional<String>): List<String> = throw LuaException("KV storage is disabled in server configuration")

    override fun incr(ownerUUID: String, key: String, value: Double): Double = throw LuaException("KV storage is disabled in server configuration")
    override fun setOnKeyDeletedHook(hook: KVKeyDeletedHook) {
    }

    override fun setOnKeyChangedHook(hook: KVKeyChangedHook) {
    }
}
