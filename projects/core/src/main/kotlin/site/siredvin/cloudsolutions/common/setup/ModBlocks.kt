package site.siredvin.cloudsolutions.common.setup

import net.minecraft.world.item.Item
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.item.HiddenDescriptiveBlockItem
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.cloudsolutions.common.block.CrafkaBroker
import site.siredvin.cloudsolutions.common.blockentity.KVStorageBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.StatsDBridgeBlockEntity
import site.siredvin.cloudsolutions.common.blockentity.TSDBStorageBlockEntity
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.util.TooltipCollection
import site.siredvin.cloudsolutions.xplat.ModPlatform

object ModBlocks {

    val KV_STORAGE: RegistryEntry<GenericBlockEntityBlock<KVStorageBlockEntity>> = ModPlatform.registerBlock(
        "kv_storage",
        { GenericBlockEntityBlock({ ModBlockEntityTypes.KV_STORAGE }, true) },
        {
            HiddenDescriptiveBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableKVStorage,
                alwaysShow = true,
            )
        },
    )

    val CRAFKA_BROKER = ModPlatform.registerBlock(
        "crafka_broker",
        { CrafkaBroker() },
        {
            HiddenDescriptiveBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableCrafkaBroker,
                alwaysShow = true,
            )
        },
    )

    val TSDB_STORAGE: RegistryEntry<GenericBlockEntityBlock<TSDBStorageBlockEntity>> = ModPlatform.registerBlock(
        "tsdb_storage",
        { GenericBlockEntityBlock({ ModBlockEntityTypes.TSDB_STORAGE }, true) },
        {
            HiddenDescriptiveBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableTSDBStorage,
                alwaysShow = true,
                TooltipCollection::unfinishedAndDisabled,
            )
        },
    )

    val STATSD_BRIDGE: RegistryEntry<GenericBlockEntityBlock<StatsDBridgeBlockEntity>> = ModPlatform.registerBlock(
        "statsd_bridge",
        { GenericBlockEntityBlock({ ModBlockEntityTypes.STATSD_BRIDGE }, true) },
        {
            HiddenDescriptiveBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableTSDBStorage,
                alwaysShow = true,
            )
        },
    )
    fun doSomething() {}
}
