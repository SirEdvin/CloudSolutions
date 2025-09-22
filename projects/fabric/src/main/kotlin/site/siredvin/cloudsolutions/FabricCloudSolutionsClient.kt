package site.siredvin.cloudsolutions

import dan200.computercraft.api.client.FabricComputerCraftAPIClient
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.resources.ResourceLocation

object FabricCloudSolutionsClient : ClientModInitializer {
    override fun onInitializeClient() {
        CloudSolutionsClientCore.onInit()
        ModelLoadingPlugin.register {
            it.addModels(CloudSolutionsClientCore.EXTRA_MODELS.map { id -> ResourceLocation(CloudSolutionsCore.MOD_ID, id) })
        }
        CloudSolutionsClientCore.onModelRegister { serializer, modeller ->
            @Suppress("UNCHECKED_CAST")
            FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(serializer as TurtleUpgradeSerialiser<ITurtleUpgrade>, modeller)
        }
    }
}
