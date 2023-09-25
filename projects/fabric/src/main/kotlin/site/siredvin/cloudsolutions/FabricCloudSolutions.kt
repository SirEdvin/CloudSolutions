package site.siredvin.cloudsolutions
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraftforge.fml.config.ModConfig
import site.siredvin.cloudsolutions.common.configuration.ConfigHolder
import site.siredvin.cloudsolutions.fabric.FabricModPlatform
import site.siredvin.cloudsolutions.fabric.FabricModRecipeIngredients
import site.siredvin.cloudsolutions.xplat.ModCommonHooks
import site.siredvin.peripheralium.FabricPeripheralium

@Suppress("UNUSED")
object FabricCloudSolutions : ModInitializer {

    override fun onInitialize() {
        // Register configuration
        FabricPeripheralium.sayHi()
        CloudSolutionsCore.configure(FabricModPlatform, FabricModRecipeIngredients)
        // Register items and blocks
        ModCommonHooks.onRegister()
        // Pretty important to setup configuration after integration loading!
        ForgeConfigRegistry.INSTANCE.register(CloudSolutionsCore.MOD_ID, ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)
        ForgeConfigRegistry.INSTANCE.register(CloudSolutionsCore.MOD_ID, ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC, "${CloudSolutionsCore.MOD_ID}_server.toml")

        ServerLifecycleEvents.SERVER_STARTED.register {
            ModCommonHooks.onServerStarted(it)
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            ModCommonHooks.onServerStopping(it)
        }
    }
}
