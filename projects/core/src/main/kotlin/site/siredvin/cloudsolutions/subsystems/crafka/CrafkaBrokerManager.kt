package site.siredvin.cloudsolutions.subsystems.crafka

import dan200.computercraft.api.lua.MethodResult
import net.minecraft.server.MinecraftServer
import java.util.concurrent.ScheduledExecutorService

interface CrafkaBrokerManager {
    fun init(server: MinecraftServer, executor: ScheduledExecutorService) {}
    fun stop(server: MinecraftServer, executor: ScheduledExecutorService) {}
    fun createTopic(ownerUUID: String, topic: String, messageLimit: Int): MethodResult
    fun listTopics(ownerUUID: String): List<String>
    fun deleteTopic(ownerUUID: String, topic: String): MethodResult
    fun publish(ownerUUID: String, topic: String, message: String): MethodResult
    fun fetchMessages(ownerUUID: String, topic: String, index: Int): Map<Int, String>
}