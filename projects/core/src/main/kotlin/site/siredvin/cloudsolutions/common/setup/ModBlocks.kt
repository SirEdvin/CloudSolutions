package site.siredvin.cloudsolutions.common.setup

import net.minecraft.world.item.Item
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.item.HiddenDescriptiveBlockItem
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import site.siredvin.cloudsolutions.util.TooltipCollection
import site.siredvin.cloudsolutions.xplat.ModPlatform

object ModBlocks {

    val KV_STORAGE = ModPlatform.registerBlock(
        "kv_storage",
        { GenericBlockEntityBlock({ ModBlockEntityTypes.KV_STORAGE.get() }, true) },
        {
            HiddenDescriptiveBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableKVStorage,
                alwaysShow = true,
            )
        },
    )

    val TSDB_STORAGE = ModPlatform.registerBlock(
        "tsdb_storage",
        { GenericBlockEntityBlock({ ModBlockEntityTypes.TSDB_STORAGE.get() }, true) },
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

    val STATSD_BRIDGE = ModPlatform.registerBlock(
        "statsd_bridge",
        { GenericBlockEntityBlock({ ModBlockEntityTypes.STATSD_BRIDGE.get() }, true) },
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
