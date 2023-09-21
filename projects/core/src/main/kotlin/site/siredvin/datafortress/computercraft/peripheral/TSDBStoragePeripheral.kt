package site.siredvin.datafortress.computercraft.peripheral

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.subsystems.tsdb.TSDBManager
import site.siredvin.peripheralium.api.peripheral.IPeripheralOwner
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral
import java.util.*

class TSDBStoragePeripheral(owner: IPeripheralOwner) : OwnedPeripheral<IPeripheralOwner>(TYPE, owner) {
    companion object {
        const val TYPE = "tsdb_storage"
    }

    override val isEnabled: Boolean
        get() = ModConfig.enableTSDBStorage

    @LuaFunction
    fun registerTimeseries(name: String, tags: Map<*, *>, retention: Optional<Int>): String {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        return DataFortressCore.tsdbManager.getOrCreate(
            player.stringUUID,
            name,
            tags.entries.associate {
                Pair(it.key.toString(), it.value.toString())
            },
            retention.orElse(TSDBManager.DEFAULT_RETENTION),
        ).toString()
    }

    @Suppress("UNUSED_PARAMETER")
    @LuaFunction
    fun queryTimeseries(namePattern: String, tagsQuery: Map<*, *>, fromTimestamp: Long, toTimestamp: Long): Map<String, Map<Long, Double>> {
        @Suppress("UNUSED_VARIABLE")
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
//        return DataFortressCore.tsdbManager.query(
//            player.stringUUID,
//            namePattern,
//            tagsQuery.entries.associate {
//                Pair(it.key.toString(), it.value.toString())
//            },
//            Instant.ofEpochSecond(fromTimestamp),
//            Instant.ofEpochSecond(toTimestamp),
//        )
        return emptyMap()
    }

    @LuaFunction
    fun getTimeseries(): List<Map<String, Any>> {
        @Suppress("UNUSED_VARIABLE")
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
//        return DataFortressCore.tsdbManager.list(player.stringUUID)
        return emptyList()
    }

    @LuaFunction
    fun postMeasurement(id: String, value: Double) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        DataFortressCore.tsdbManager.put(player.stringUUID, UUID.fromString(id), value)
    }

    @LuaFunction
    fun postMeasurement(values: Map<*, *>) {
        val player = peripheralOwner.owner ?: throw LuaException("Cannot find attached player to this peripheral")
        DataFortressCore.tsdbManager.putMany(
            player.stringUUID,
            values.entries.associate {
                UUID.fromString(it.key.toString()) to (it.value as Number).toDouble()
            },
        )
    }
}
