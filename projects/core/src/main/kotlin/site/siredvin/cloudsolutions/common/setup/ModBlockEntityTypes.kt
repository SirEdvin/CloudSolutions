package site.siredvin.cloudsolutions.common.setup

import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.cloudsolutions.common.blockentity.CrafkaBrokerBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.KVStorageBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.StatsDBridgeBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.TSDBStorageBlockEntity
import site.siredvin.cloudsolutions.xplat.ModPlatform

object ModBlockEntityTypes {
    val KV_STORAGE: RegistryEntry<BlockEntityType<KVStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        "kv_storage",
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::KVStorageBlockEntity,
            ModBlocks.KV_STORAGE.get(),
        )
    }

    val CRAFKA_BROKER: RegistryEntry<BlockEntityType<CrafkaBrokerBlockEntity>> = ModPlatform.registerBlockEntity(
        "crafka_broker",
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::CrafkaBrokerBlockEntity,
            ModBlocks.CRAFKA_BROKER.get(),
        )
    }

    val TSDB_STORAGE: RegistryEntry<BlockEntityType<TSDBStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        "tsdb_storage",
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::TSDBStorageBlockEntity,
            ModBlocks.TSDB_STORAGE.get(),
        )
    }

    val STATSD_BRIDGE: RegistryEntry<BlockEntityType<StatsDBridgeBlockEntity>> = ModPlatform.registerBlockEntity(
        "statsd_bridge",
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::StatsDBridgeBlockEntity,
            ModBlocks.STATSD_BRIDGE.get(),
        )
    }

    fun doSomething() {}
}
