package site.siredvin.cloudsolutions.util

import net.minecraft.network.chat.Component
import site.siredvin.cloudsolutions.data.ModText
import site.siredvin.peripheralium.common.items.PeripheralBlockItem

object TooltipCollection {
    fun unfinishedAndDisabled(@Suppress("UNUSED_PARAMETER") item: PeripheralBlockItem): List<Component> {
        return listOf(
            ModText.UNFINISHED_AND_DISABLED.text,
        )
    }
}
