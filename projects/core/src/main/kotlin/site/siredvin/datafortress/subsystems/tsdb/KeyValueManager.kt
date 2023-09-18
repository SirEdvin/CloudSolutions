package site.siredvin.datafortress.subsystems.tsdb

import java.time.Instant
import java.util.UUID

interface KeyValueManager {
    fun init()
    fun put(ownerUUID: String, key: String, value: String, expire: Instant? = null)
    fun get(ownerUUID: String, key: String): String?
    fun getExpire(ownerUUID: String, key: String): Instant?
    fun putExpire(ownerUUID: String, key: String, expire: Instant? = null)
    fun list(ownerUUID: String): List<String>
}