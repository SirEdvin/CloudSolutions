package site.siredvin.datafortress

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = DataFortressCore.MOD_ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeDataFortressClient {

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onClientSetup(event: FMLClientSetupEvent) {
        DataFortressClientCore.onInit()
    }

    @SubscribeEvent
    fun registerModels(event: RegisterAdditional) {
        DataFortressClientCore.registerExtraModels { model: ResourceLocation ->
            event.register(model)
        }
    }
}
