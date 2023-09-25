package site.siredvin.cloudsolutions.forge

import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.xplat.ModCommonHooks

@Mod.EventBusSubscriber(modid = CloudSolutionsCore.MOD_ID)
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
