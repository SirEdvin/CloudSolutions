package site.siredvin.cloudsolutions.data

import site.siredvin.broccolium.modules.data.api.GeneratorSink

object ModDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add {
            ModRecipeProvider(it)
        }
        generator.lootTable(ModLootTableProvider.getTables())
        generator.models(ModBlockModelProvider::addModels, ModItemModelProvider::addModels)
        generator.add(::ModEnLanguageProvider)
        generator.add(::ModUaLanguageProvider)
    }
}
