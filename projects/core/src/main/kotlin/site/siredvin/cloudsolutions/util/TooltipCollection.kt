package site.siredvin.cloudsolutions.util

import net.minecraft.network.chat.Component
import site.siredvin.broccolium.modules.base.item.HiddenDescriptiveBlockItem
import site.siredvin.cloudsolutions.data.ModText

object TooltipCollection {
    fun unfinishedAndDisabled(@Suppress("UNUSED_PARAMETER") item: HiddenDescriptiveBlockItem): List<Component> = listOf(
        ModText.UNFINISHED_AND_DISABLED.text,
    )
}
