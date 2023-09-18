package site.siredvin.datafortress.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral
import java.time.Instant
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class DataStoragePeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "data_storage"
    }

    override val isEnabled: Boolean
        // TODO: replace
        get() = true

    @LuaFunction
    fun put(key: String, value: String, expire: Optional<Long>) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        DataFortressCore.kvManager.put(player.stringUUID, key, value, expire.map { Instant.ofEpochSecond(it) }.getOrNull())
    }

    @LuaFunction
    fun get(key: String): String? {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return DataFortressCore.kvManager.get(player.stringUUID, key)
    }

    @LuaFunction
    fun getExpire(key: String): Long? {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return DataFortressCore.kvManager.getExpire(player.stringUUID, key)?.epochSecond
    }

    @LuaFunction
    fun putExpire(key: String, expire: Optional<Long>) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        DataFortressCore.kvManager.putExpire(player.stringUUID, key, expire.map { Instant.ofEpochSecond(it) }.getOrNull())
    }

    @LuaFunction
    fun list(): List<String> {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return DataFortressCore.kvManager.list(player.stringUUID)
    }
}