package site.siredvin.cloudsolutions.subsystems.kv

import dan200.computercraft.api.lua.MethodResult
import net.minecraft.server.MinecraftServer
import java.time.Instant
import java.util.Optional
import java.util.concurrent.ScheduledExecutorService

interface KeyValueManager {
    fun init(server: MinecraftServer, executor: ScheduledExecutorService) {}
    fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {}
    fun put(ownerUUID: String, key: String, value: String, expire: Instant? = null): MethodResult
    fun mput(ownerUUID: String, values: Map<String, String>): MethodResult
    fun delete(ownerUUID: String, key: String): MethodResult
    fun get(ownerUUID: String, key: String): String?
    fun mget(ownerUUID: String, keys: List<String>): Map<String, String>
    fun getExpire(ownerUUID: String, key: String): Instant?
    fun putExpire(ownerUUID: String, key: String, expire: Instant? = null): MethodResult
    fun list(ownerUUID: String, glob: Optional<String>): List<String>
    fun incr(ownerUUID: String, key: String, value: Double): Double

    fun setOnKeyDeletedHook(hook: KVKeyDeletedHook)
    fun setOnKeyChangedHook(hook: KVKeyChangedHook)
}
