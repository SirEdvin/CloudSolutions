package site.siredvin.cloudsolutions.computercraft.peripheral

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.resources.ResourceLocation
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner

class CrafkaBrokerPeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "crafka_broker"
        val ID = ResourceLocation(CloudSolutionsCore.MOD_ID, TYPE)
    }

    data class Subscription(val isEphemeral: Boolean, val autoCursor: Boolean, var cursor: Int)

    private val subscriptions: MutableMap<String, MutableMap<Int, Subscription>> = mutableMapOf()

    override val isEnabled: Boolean
        get() = ModConfig.enableCrafkaBroker

    override val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data = super.peripheralConfiguration
            return data
        }

    override fun detach(computer: IComputerAccess) {
        super.detach(computer)
        // Here we should do ephemeral handling
        val player = peripheralOwner.ownerUUID
        if (player != null) {
            SubsystemManager.crafkaBrokerManager?.onComputerDetach(player.toString(), computer.id)
        }
    }

    override fun attach(computer: IComputerAccess) {
        super.attach(computer)
        val player = peripheralOwner.ownerUUID
        if (player != null) {
            SubsystemManager.crafkaBrokerManager?.onComputerAttach(player.toString(), computer.id)
        }
    }

    @LuaFunction
    fun createTopic(arguments: IArguments): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        val topic = arguments.getString(0)
        val messageLimit = arguments.optInt(1, ModConfig.crafkaTopicSizeLimit)
        return SubsystemManager.crafkaBrokerManager!!.createTopic(player.toString(), topic, messageLimit.coerceAtMost(ModConfig.crafkaTopicSizeLimit))
    }

    @LuaFunction
    fun listTopics(): List<String> {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.listTopics(player.toString())
    }

    @LuaFunction
    fun deleteTopic(topic: String): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.deleteTopic(player.toString(), topic)
    }

    @LuaFunction
    fun publish(topic: String, message: String): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.publish(player.toString(), topic, message)
    }

    @LuaFunction
    fun fetchMessages(topic: String, index: Int): Map<Int, String> {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.fetchMessages(player.toString(), topic, index)
    }

    @LuaFunction
    fun describeTopic(topic: String): Map<String, Any> {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.describeTopic(player.toString(), topic)
    }

    @LuaFunction
    fun subscribe(access: IComputerAccess, topic: String, optOptions: Map<*, *>?): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        val options = optOptions ?: emptyMap<String, Any>()
        val fragile = (options["fragile"] as? Boolean) ?: false
        val autoCursor = (options["autoCursor"] as? Boolean) ?: true
        val cursor = (options["cursor"] as? Number)?.toInt() ?: 0
        return SubsystemManager.crafkaBrokerManager!!.subscribe(player.toString(), topic, access.id, cursor, fragile, autoCursor)
    }

    @LuaFunction
    fun unsubscribe(access: IComputerAccess, topic: String): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.unsubscribe(player.toString(), topic, access.id)
    }

    @LuaFunction
    fun getSubscription(access: IComputerAccess, topic: String): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.getSubscription(player.toString(), topic, access.id)
    }

    @LuaFunction
    fun setCursor(access: IComputerAccess, topic: String, value: Int): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.setCursor(player.toString(), topic, access.id, value)
    }
}
