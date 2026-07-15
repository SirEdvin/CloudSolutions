package site.siredvin.cloudsolutions

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional

@EventBusSubscriber(modid = CloudSolutionsCore.MOD_ID, value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.MOD)
object ForgeCloudSolutionsClient {

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork(CloudSolutionsClientCore::onInit)
    }

    @SubscribeEvent
    fun registerModels(event: RegisterAdditional) {
        CloudSolutionsClientCore.registerExtraModels { model: ResourceLocation ->
            event.register(ModelResourceLocation.standalone(model))
        }
    }

    @SubscribeEvent
    fun registerTurtleModels(event: RegisterTurtleModellersEvent) {
        CloudSolutionsClientCore.onModelRegister { serializer, model ->
            @Suppress("UNCHECKED_CAST")
            event.register(
                serializer as UpgradeType<ITurtleUpgrade>,
                model as TurtleUpgradeModeller<ITurtleUpgrade>,
            )
        }
    }
}
