package site.siredvin.datafortress

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.datafortress.common.setup.Blocks
import site.siredvin.datafortress.data.ModText
import site.siredvin.datafortress.subsystems.tsdb.TSDBManager
import site.siredvin.datafortress.subsystems.tsdb.sqlite.TSDBSQLiteManager
import site.siredvin.datafortress.xplat.ModCommonHooks
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.datafortress.xplat.ModRecipeIngredients
import site.siredvin.peripheralium.xplat.BaseInnerPlatform
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

object DataFortressCore {
    const val MOD_ID = "datafortress"

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
