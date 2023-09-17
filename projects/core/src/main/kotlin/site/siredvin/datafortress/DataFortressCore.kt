package site.siredvin.datafortress

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.datafortress.common.setup.Items
import site.siredvin.datafortress.data.ModText
import site.siredvin.datafortress.xplat.ModCommonHooks
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.datafortress.xplat.ModRecipeIngredients
import site.siredvin.peripheralium.xplat.BaseInnerPlatform

object DataFortressCore {
    const val MOD_ID = "datafortress"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder {
        return builder.icon { Items.TEMPLATE_ITEM.get().defaultInstance }
            .title(ModText.CREATIVE_TAB.text)
            .displayItems { _, output ->
                ModPlatform.holder.blocks.forEach { output.accept(it.get()) }
                ModPlatform.holder.items.forEach { output.accept(it.get()) }
                ModCommonHooks.registerUpgradesInCreativeTab(output)
            }
    }

    fun configure(platform: BaseInnerPlatform, ingredients: ModRecipeIngredients) {
        ModPlatform.configure(platform)
        ModRecipeIngredients.configure(ingredients)
    }
}
