package site.siredvin.datafortress
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraftforge.fml.config.ModConfig
import site.siredvin.datafortress.common.configuration.ConfigHolder
import site.siredvin.datafortress.fabric.FabricModPlatform
import site.siredvin.datafortress.fabric.FabricModRecipeIngredients
import site.siredvin.datafortress.xplat.ModCommonHooks
import site.siredvin.peripheralium.FabricPeripheralium

@Suppress("UNUSED")
object FabricDataFortress : ModInitializer {

    override fun onInitialize() {
        // Register configuration
        FabricPeripheralium.sayHi()
        DataFortressCore.configure(FabricModPlatform, FabricModRecipeIngredients)
        // Register items and blocks
        ModCommonHooks.onRegister()
        // Pretty important to setup configuration after integration loading!
        ForgeConfigRegistry.INSTANCE.register(DataFortressCore.MOD_ID, ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)

        ServerLifecycleEvents.SERVER_STARTED.register {
            ModCommonHooks.onServerStarted()
        }
    }
}
