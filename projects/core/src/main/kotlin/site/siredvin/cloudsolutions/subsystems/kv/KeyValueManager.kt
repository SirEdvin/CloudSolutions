package site.siredvin.cloudsolutions.subsystems.kv

import net.minecraft.server.MinecraftServer
import java.time.Instant
import java.util.concurrent.ScheduledExecutorService

interface KeyValueManager {
    fun init(server: MinecraftServer, executor: ScheduledExecutorService) {}
    fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {}
    fun put(ownerUUID: String, key: String, value: String, expire: Instant? = null)
    fun delete(ownerUUID: String, key: String)
    fun get(ownerUUID: String, key: String): String?
    fun getExpire(ownerUUID: String, key: String): Instant?
    fun putExpire(ownerUUID: String, key: String, expire: Instant? = null)
    fun list(ownerUUID: String): List<String>
}
