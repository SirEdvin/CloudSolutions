package site.siredvin.cloudsolutions

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.resources.ResourceLocation

object FabricCloudSolutionsClient : ClientModInitializer {
    override fun onInitializeClient() {
        CloudSolutionsClientCore.onInit()
        ModelLoadingPlugin.register {
            it.addModels(CloudSolutionsClientCore.EXTRA_MODELS.map { id -> ResourceLocation(CloudSolutionsCore.MOD_ID, id) })
        }
    }
}
