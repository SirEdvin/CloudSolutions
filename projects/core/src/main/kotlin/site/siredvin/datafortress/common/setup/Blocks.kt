package site.siredvin.datafortress.common.setup

import net.minecraft.world.item.Item
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.common.blocks.GenericBlockEntityBlock
import site.siredvin.peripheralium.common.items.PeripheralBlockItem

object Blocks {

    val DATA_STORAGE = ModPlatform.registerBlock(
        "data_storage",
        { GenericBlockEntityBlock({ BlockEntityTypes.DATA_STORAGE.get() }, true)},
        {
            PeripheralBlockItem(
                it,
                Item.Properties(),
                // TODO: update
                { true },
                // TODO: add something
            )
        }
    )

    val TSDB_STORAGE = ModPlatform.registerBlock(
        "tsdb_storage",
        { GenericBlockEntityBlock({ BlockEntityTypes.TSDB_STORAGE.get() }, true) },
        {
            PeripheralBlockItem(
                it,
                Item.Properties(),
                // TODO: update
                { true },
                alwaysShow = false,
                // TODO: add something (?)
//                TooltipCollection::isDisabled,
//                TooltipCollection::universalScanningRadius,
            )
        },
    )
    fun doSomething() {}
}
