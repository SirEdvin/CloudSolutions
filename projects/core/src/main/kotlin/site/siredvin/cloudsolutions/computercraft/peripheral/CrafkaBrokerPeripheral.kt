package site.siredvin.cloudsolutions.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.resources.ResourceLocation
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.SubscriptionManager
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner

class CrafkaBrokerPeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "crafka_broker"
        val ID = ResourceLocation(CloudSolutionsCore.MOD_ID, TYPE)
    }

    init {
        val ownerUUID = this.peripheralOwner.ownerUUID?.toString()
        if (ownerUUID != null)
            SubscriptionManager.addCrafkaBroker(ownerUUID, this)
    }

    override val isEnabled: Boolean
        get() = ModConfig.enableCrafkaBroker

    override val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data = super.peripheralConfiguration
            return data
        }

    @LuaFunction
    fun createTopic(topic: String, messageLimit: Int): MethodResult {
        val player = peripheralOwner.ownerUUID ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.crafkaBrokerManager!!.createTopic(player.toString(), topic, messageLimit)
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
}
