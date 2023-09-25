package site.siredvin.cloudsolutions.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral
import java.time.Instant
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class KVStoragePeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "kv_storage"
    }

    override val isEnabled: Boolean
        get() = ModConfig.enableKVStorage

    @LuaFunction
    fun put(key: String, value: String, expire: Optional<Long>) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        SubsystemManager.kvManager?.put(player.stringUUID, key, value, expire.map { Instant.ofEpochSecond(it) }.getOrNull())
    }

    @LuaFunction
    fun get(key: String): String? {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.get(player.stringUUID, key)
    }

    @LuaFunction("get_ex")
    fun getEx(key: String): Long? {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.getExpire(player.stringUUID, key)?.epochSecond
    }

    @LuaFunction("put_ex")
    fun putEx(key: String, expire: Optional<Long>) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        SubsystemManager.kvManager?.putExpire(player.stringUUID, key, expire.map { Instant.ofEpochSecond(it) }.getOrNull())
    }

    @LuaFunction
    fun list(): List<String> {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return SubsystemManager.kvManager?.list(player.stringUUID) ?: emptyList()
    }
}
