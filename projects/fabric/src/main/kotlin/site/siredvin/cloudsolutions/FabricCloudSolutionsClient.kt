package site.siredvin.cloudsolutions

import dan200.computercraft.api.client.FabricComputerCraftAPIClient
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.resources.ResourceLocation

object FabricCloudSolutionsClient : ClientModInitializer {
    override fun onInitializeClient() {
        CloudSolutionsClientCore.onInit()
        ModelLoadingPlugin.register {
            it.addModels(CloudSolutionsClientCore.EXTRA_MODELS.map { id -> ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, id) })
        }
        CloudSolutionsClientCore.onModelRegister { type, modeller ->
            @Suppress("UNCHECKED_CAST")
            FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(
                type as UpgradeType<ITurtleUpgrade>,
                modeller as TurtleUpgradeModeller<ITurtleUpgrade>,
            )
        }
    }
}
