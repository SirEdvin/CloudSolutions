package site.siredvin.cloudsolutions.forge

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.xplat.ModCommonHooks

@EventBusSubscriber(modid = CloudSolutionsCore.MOD_ID)
object ForgeCommonHooks {
    @SubscribeEvent
    fun onServerStarted(event: ServerStartedEvent) {
        ModCommonHooks.onServerStarted(event.server)
    }

    @SubscribeEvent
    fun onServerStopping(event: ServerStoppingEvent) {
        ModCommonHooks.onServerStopping(event.server)
    }
}
