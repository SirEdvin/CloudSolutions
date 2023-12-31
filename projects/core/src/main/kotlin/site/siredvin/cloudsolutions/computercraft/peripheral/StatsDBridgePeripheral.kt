package site.siredvin.cloudsolutions.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.statsq.StatsDClient
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral

class StatsDBridgePeripheral(peripheralOwner: IPeripheralOwner) :
    OwnedPeripheral<IPeripheralOwner>(TYPE, peripheralOwner) {
    companion object {
        const val TYPE = "statsd_bridge"
    }
    override val isEnabled: Boolean
        get() = ModConfig.enableStatsDBridge

    override val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val data = super.peripheralConfiguration
            data["playerRateLimit"] = ModConfig.statsdPlayerRateLimit
            data["globalRateLimit"] = ModConfig.statsdGlobalRateLimit
            return data
        }

    @LuaFunction
    fun count(aspect: String, delta: Long) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find owner")
        val playerSpecificAspect = "${player.name.string}.$aspect"
        StatsDClient.count(player.stringUUID, playerSpecificAspect, delta)
    }

    @LuaFunction
    fun delta(aspect: String, value: Long) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find owner")
        val playerSpecificAspect = "${player.name.string}.$aspect"
        StatsDClient.delta(player.stringUUID, playerSpecificAspect, value)
    }

    @LuaFunction
    fun gauge(aspect: String, value: Long) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find owner")
        val playerSpecificAspect = "${player.name.string}.$aspect"
        StatsDClient.gauge(player.stringUUID, playerSpecificAspect, value)
    }

    @LuaFunction
    fun set(aspect: String, eventName: String) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find owner")
        val playerSpecificAspect = "${player.name.string}.$aspect"
        StatsDClient.set(player.stringUUID, playerSpecificAspect, eventName)
    }

    @LuaFunction
    fun time(aspect: String, timeInMs: Long) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find owner")
        val playerSpecificAspect = "${player.name.string}.$aspect"
        StatsDClient.time(player.stringUUID, playerSpecificAspect, timeInMs)
    }
}
