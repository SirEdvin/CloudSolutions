package site.siredvin.cloudsolutions.data

import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.tweakium.modules.data.upgrades

object ModDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(::ModRecipeProvider)
        generator.lootTable(ModLootTableProvider.getTables())
        generator.models(ModBlockModelProvider::addModels, ModItemModelProvider::addModels)
        generator.add(::ModEnLanguageProvider)
        generator.add(::ModUaLanguageProvider)
        generator.upgrades(CloudSolutionsCore.MOD_ID, ModPocketUpgradeDataProvider, ModTurtleUpgradeDataProvider)
    }
}
