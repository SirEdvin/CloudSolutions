package site.siredvin.cloudsolutions

import net.minecraft.resources.ResourceLocation
import java.util.function.Consumer

object CloudSolutionsClientCore {
    val EXTRA_MODELS = emptyArray<String>()

    fun registerExtraModels(register: Consumer<ResourceLocation>) {
        EXTRA_MODELS.forEach { register.accept(ResourceLocation(CloudSolutionsCore.MOD_ID, it)) }
    }

    fun onInit() {
    }
}
