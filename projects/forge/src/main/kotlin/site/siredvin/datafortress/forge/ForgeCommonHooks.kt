package site.siredvin.datafortress.forge

import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.xplat.ModCommonHooks

@Mod.EventBusSubscriber(modid = DataFortressCore.MOD_ID)
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
