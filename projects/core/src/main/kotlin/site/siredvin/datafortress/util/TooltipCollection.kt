package site.siredvin.datafortress.util

import net.minecraft.network.chat.Component
import site.siredvin.datafortress.data.ModText
import site.siredvin.peripheralium.common.items.PeripheralBlockItem

object TooltipCollection {
    fun unfinishedAndDisabled(@Suppress("UNUSED_PARAMETER") item: PeripheralBlockItem): List<Component> {
        return listOf(
            ModText.UNFINISHED_AND_DISABLED.text,
        )
    }
}
