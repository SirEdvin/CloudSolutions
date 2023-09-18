package site.siredvin.datafortress.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.datafortress.subsystems.tsdb.TSDBManager
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral
import java.util.*

class TSDBStoragePeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "tsdb_storage"
    }

    override val isEnabled: Boolean
        // TOOD: update
        get() = true

    @LuaFunction
    fun registerTimeseries(name: String, tags: Map<*, *>, retention: Optional<Int>): Int {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return TSDBManager.registerTimeseriers(
            player.stringUUID,
            name,
            tags.entries.associate {
                Pair(it.key.toString(), it.value.toString())
            },
            retention.orElse(TSDBManager.DEFAULT_RETENTION),
        ).value
    }

    @LuaFunction
    fun queryTimeseries(namePattern: String, tagsQuery: Map<*, *>, fromTimestamp: Long, toTimestamp: Long): Map<String, Map<Long, Double>> {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return TSDBManager.queryTimeseries(
            player.stringUUID,
            namePattern,
            tagsQuery.entries.associate {
                Pair(it.key.toString(), it.value.toString())
            },
            fromTimestamp,
            toTimestamp,
        )
    }

    @LuaFunction
    fun getTimeseries(): List<Map<String, Any>> {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return TSDBManager.getTimeseries(player.stringUUID)
    }

    @LuaFunction
    fun postMeasurement(id: Int, value: Double) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        TSDBManager.addMeasurement(player.stringUUID, id, value)
    }
}
