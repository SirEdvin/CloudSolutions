package site.siredvin.cloudsolutions

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.cloudsolutions.common.setup.Blocks
import site.siredvin.cloudsolutions.data.ModText
import site.siredvin.cloudsolutions.subsystems.tsdb.TSDBManager
import site.siredvin.cloudsolutions.subsystems.tsdb.sqlite.TSDBSQLiteManager
import site.siredvin.cloudsolutions.xplat.ModCommonHooks
import site.siredvin.cloudsolutions.xplat.ModPlatform
import site.siredvin.cloudsolutions.xplat.ModRecipeIngredients
import site.siredvin.peripheralium.xplat.BaseInnerPlatform
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

object CloudSolutionsCore {
    const val MOD_ID = "cloudsolutions"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)
    val tsdbManager: TSDBManager by lazy {
        // TODO: some logic for TSDBManager calculation from settings
        TSDBSQLiteManager
    }
    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder {
        return builder.icon { Blocks.STATSD_BRIDGE.get().asItem().defaultInstance }
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
        PeripheraliumPlatform.registerGenericPeripheralLookup()
        tsdbManager.init()
    }
}
