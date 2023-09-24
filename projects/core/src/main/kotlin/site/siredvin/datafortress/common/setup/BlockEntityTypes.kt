package site.siredvin.datafortress.common.setup

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.common.blockentity.KVStorageBlockEntity
import site.siredvin.datafortress.common.blockentity.StatsDBridgeBlockEntity
import site.siredvin.datafortress.common.blockentity.TSDBStorageBlockEntity
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.function.Supplier

object BlockEntityTypes {
    val KV_STORAGE: Supplier<BlockEntityType<KVStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(DataFortressCore.MOD_ID, "kv_storage"),
    ) {
        PeripheraliumPlatform.createBlockEntityType(
            ::KVStorageBlockEntity,
            Blocks.KV_STORAGE.get(),
        )
    }

    val TSDB_STORAGE: Supplier<BlockEntityType<TSDBStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(DataFortressCore.MOD_ID, "tsdb_storage"),
    ) {
        PeripheraliumPlatform.createBlockEntityType(
            ::TSDBStorageBlockEntity,
            Blocks.TSDB_STORAGE.get(),
        )
    }

    val STATSD_BRIDGE: Supplier<BlockEntityType<StatsDBridgeBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(DataFortressCore.MOD_ID, "statsd_bridge"),
    ) {
        PeripheraliumPlatform.createBlockEntityType(
            ::StatsDBridgeBlockEntity,
            Blocks.STATSD_BRIDGE.get(),
        )
    }

    fun doSomething() {}
}
