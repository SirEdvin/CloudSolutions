package site.siredvin.datafortress

import net.minecraft.resources.ResourceLocation
import java.util.function.Consumer

object DataFortressClientCore {
    val EXTRA_MODELS = emptyArray<String>()

    fun registerExtraModels(register: Consumer<ResourceLocation>) {
        EXTRA_MODELS.forEach { register.accept(ResourceLocation(DataFortressCore.MOD_ID, it)) }
    }

    fun onInit() {
    }
}
