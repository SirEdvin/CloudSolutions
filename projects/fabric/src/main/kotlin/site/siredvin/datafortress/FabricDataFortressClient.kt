package site.siredvin.datafortress

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.resources.ResourceLocation

object FabricDataFortressClient : ClientModInitializer {
    override fun onInitializeClient() {
        DataFortressClientCore.onInit()
        ModelLoadingPlugin.register {
            it.addModels(DataFortressClientCore.EXTRA_MODELS.map { id -> ResourceLocation(DataFortressCore.MOD_ID, id) })
        }
    }
}
