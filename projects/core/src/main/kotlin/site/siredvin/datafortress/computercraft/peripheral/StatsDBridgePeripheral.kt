package site.siredvin.datafortress.computercraft.peripheral

import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.subsystems.statsq.StatsDClient
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral

class StatsDBridgePeripheral(peripheralOwner: IPeripheralOwner) :
    OwnedPeripheral<IPeripheralOwner>(TYPE, peripheralOwner) {
    companion object {
        const val TYPE = "statsd_bridge"
    }
    override val isEnabled: Boolean
        get() = ModConfig.enableStatsDBridge

    // TODO: add ownerUUID/Player name mixing

    @LuaFunction
    fun count(aspect: String, delta: Long) = StatsDClient.count(aspect, delta)

    @LuaFunction
    fun increment(aspect: String) = StatsDClient.increment(aspect)

    @LuaFunction
    fun decrement(aspect: String) = StatsDClient.decrement(aspect)

    @LuaFunction
    fun gauge(aspect: String, value: Long) = StatsDClient.gauge(aspect, value)

    @LuaFunction
    fun set(aspect: String, eventName: String) = StatsDClient.set(aspect, eventName)

    @LuaFunction
    fun time(aspect: String, timeInMs: Long) = StatsDClient.time(aspect, timeInMs)
}
