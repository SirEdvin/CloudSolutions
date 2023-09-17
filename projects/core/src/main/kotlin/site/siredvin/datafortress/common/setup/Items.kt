package site.siredvin.datafortress.common.setup

import net.minecraft.world.item.Item
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.common.items.DescriptiveItem

object Items {
    val TEMPLATE_ITEM = ModPlatform.registerItem("template_item") {
        DescriptiveItem(
            Item.Properties(),
        )
    }

    fun doSomething() {
    }
}
