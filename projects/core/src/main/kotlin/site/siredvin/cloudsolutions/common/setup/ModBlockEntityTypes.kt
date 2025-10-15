package site.siredvin.cloudsolutions.common.setup

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.blockentity.CrafkaBrokerBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.KVStorageBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.StatsDBridgeBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.TSDBStorageBlockEntity
import site.siredvin.cloudsolutions.xplat.ModPlatform
import java.util.function.Supplier

object ModBlockEntityTypes {
    val KV_STORAGE: Supplier<BlockEntityType<KVStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(CloudSolutionsCore.MOD_ID, "kv_storage"),
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::KVStorageBlockEntity,
            ModBlocks.KV_STORAGE.get(),
        )
    }

    val CRAFKA_BROKER: Supplier<BlockEntityType<CrafkaBrokerBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(CloudSolutionsCore.MOD_ID, "crafka_broker"),
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::CrafkaBrokerBlockEntity,
            ModBlocks.CRAFKA_BROKER.get(),
        )
    }

    val TSDB_STORAGE: Supplier<BlockEntityType<TSDBStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(CloudSolutionsCore.MOD_ID, "tsdb_storage"),
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::TSDBStorageBlockEntity,
            ModBlocks.TSDB_STORAGE.get(),
        )
    }

    val STATSD_BRIDGE: Supplier<BlockEntityType<StatsDBridgeBlockEntity>> = ModPlatform.registerBlockEntity(
        ResourceLocation(CloudSolutionsCore.MOD_ID, "statsd_bridge"),
    ) {
        PlatformToolkit.get().createBlockEntityType(
            ::StatsDBridgeBlockEntity,
            ModBlocks.STATSD_BRIDGE.get(),
        )
    }

    fun doSomething() {}
}
