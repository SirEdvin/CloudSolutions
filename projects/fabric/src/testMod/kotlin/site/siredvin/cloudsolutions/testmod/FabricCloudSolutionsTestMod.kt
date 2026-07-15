package site.siredvin.cloudsolutions.testmod

import dan200.computercraft.api.peripheral.PeripheralLookup
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.setup.ModBlockEntityTypes
import site.siredvin.cloudsolutions.fabric.FabricModPlatform
import site.siredvin.cloudsolutions.fabric.FabricModRecipeIngredients
import site.siredvin.testiarium.FabricTestiarium
import site.siredvin.testiarium.Testiarium
import site.siredvin.testiarium.cct.CctComputers
import site.siredvin.testiarium.cct.CctFixtureCommands
import site.siredvin.tweakium.modules.FabricTweakium
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider

object FabricCloudSolutionsTestMod : ModInitializer {
    override fun onInitialize() {
        FabricTweakium.sayHi()
        CloudSolutionsCore.configure(FabricModPlatform, FabricModRecipeIngredients)
        PeripheralLookup.get().registerForBlockEntities(
            { entity, direction -> (entity as? IPeripheralProvider<*>)?.getPeripheral(direction) },
            ModBlockEntityTypes.KV_STORAGE.get(),
            ModBlockEntityTypes.CRAFKA_BROKER.get(),
        )
        CctComputers.initialize()
        ServerLifecycleEvents.SERVER_STARTING.register {
            CctComputers.reset()
            CctFixtureCommands.importFiles(it)
        }
        Testiarium.register(CloudSolutionsGameTests::class.java)
        FabricTestiarium.registerTests()
    }
}
