package site.siredvin.datafortress.subsystems.kv

import dan200.computercraft.api.lua.LuaException
import java.time.Instant

object DisabledKVManager: KeyValueManager {
    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?) {
        throw LuaException("KV storage is disabled in server configuration")
    }

    override fun get(ownerUUID: String, key: String): String? {
        throw LuaException("KV storage is disabled in server configuration")
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        throw LuaException("KV storage is disabled in server configuration")
    }

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?) {
        throw LuaException("KV storage is disabled in server configuration")
    }

    override fun list(ownerUUID: String): List<String> {
        throw LuaException("KV storage is disabled in server configuration")
    }
}