package site.siredvin.datafortress.common.setup

import net.minecraft.world.item.Item
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.util.TooltipCollection
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.common.blocks.GenericBlockEntityBlock
import site.siredvin.peripheralium.common.items.PeripheralBlockItem

object Blocks {

    val DATA_STORAGE = ModPlatform.registerBlock(
        "data_storage",
        { GenericBlockEntityBlock({ BlockEntityTypes.DATA_STORAGE.get() }, true) },
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
