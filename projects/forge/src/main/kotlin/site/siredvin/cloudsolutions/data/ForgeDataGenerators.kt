package site.siredvin.cloudsolutions.data

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink
import site.siredvin.cloudsolutions.CloudSolutionsCore

@EventBusSubscriber(modid = CloudSolutionsCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object ForgeDataGenerators {
    @SubscribeEvent
    fun genData(event: GatherDataEvent) {
        ModDataProviders.add(ForgeGeneratorSink(event.generator, event))
    }
}
