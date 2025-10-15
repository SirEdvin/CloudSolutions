package site.siredvin.cloudsolutions.subsystems.crafka

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.MethodResult

object DisabledCrafkaManager: CrafkaBrokerManager {
    override fun createTopic(
        ownerUUID: String,
        topic: String,
        messageLimit: Int
    ): MethodResult {
        throw LuaException("Crafka broker is disabled in server configuration")
    }

    override fun listTopics(ownerUUID: String): List<String> {
        throw LuaException("Crafka broker is disabled in server configuration")
    }

    override fun deleteTopic(
        ownerUUID: String,
        topic: String
    ): MethodResult {
        throw LuaException("Crafka broker is disabled in server configuration")
    }

    override fun publish(
        ownerUUID: String,
        topic: String,
        message: String
    ): MethodResult {
        throw LuaException("Crafka broker is disabled in server configuration")
    }

    override fun fetchMessages(
        ownerUUID: String,
        topic: String,
        index: Int
    ): Map<Int, String> {
        throw LuaException("Crafka broker is disabled in server configuration")
    }
}