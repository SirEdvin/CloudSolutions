package site.siredvin.datafortress.common.setup

import net.minecraft.world.item.Item
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.util.TooltipCollection
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.common.blocks.GenericBlockEntityBlock
import site.siredvin.peripheralium.common.items.PeripheralBlockItem

object Blocks {

    val KV_STORAGE = ModPlatform.registerBlock(
        "kv_storage",
        { GenericBlockEntityBlock({ BlockEntityTypes.KV_STORAGE.get() }, true) },
        {
            PeripheralBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableKVStorage,
                alwaysShow = true,
            )
        },
    )

    val TSDB_STORAGE = ModPlatform.registerBlock(
        "tsdb_storage",
        { GenericBlockEntityBlock({ BlockEntityTypes.TSDB_STORAGE.get() }, true) },
        {
            PeripheralBlockItem(
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
        { GenericBlockEntityBlock({ BlockEntityTypes.STATSD_BRIDGE.get() }, true) },
        {
            PeripheralBlockItem(
                it,
                Item.Properties(),
                ModConfig::enableTSDBStorage,
                alwaysShow = true,
                TooltipCollection::unfinishedAndDisabled,
            )
        },
    )
    fun doSomething() {}
}
