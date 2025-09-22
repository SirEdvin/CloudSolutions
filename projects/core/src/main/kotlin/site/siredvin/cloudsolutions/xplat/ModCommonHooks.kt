package site.siredvin.cloudsolutions.xplat

import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.setup.ModBlockEntityTypes
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.common.setup.ModItems
import site.siredvin.cloudsolutions.common.setup.PocketUpgradeSerializers
import site.siredvin.cloudsolutions.common.setup.TurtleUpgradeSerializers
import site.siredvin.cloudsolutions.subsystems.SubsystemManager
import site.siredvin.tweakium.modules.platform.ComputerPlatformRegistries
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit

object ModCommonHooks {

    fun onRegister() {
        ModItems.doSomething()
        ModBlocks.doSomething()
        ModBlockEntityTypes.doSomething()
        PocketUpgradeSerializers.doSomething()
        TurtleUpgradeSerializers.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation(CloudSolutionsCore.MOD_ID, "tab"),
            CloudSolutionsCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }

    fun onServerStarted(server: MinecraftServer) {
        SubsystemManager.onServerStart(server)
    }

    fun onServerStopping(server: MinecraftServer) {
        SubsystemManager.onServerStop(server)
    }

    fun registerUpgradesInCreativeTab(output: CreativeModeTab.Output) {
        ModPlatform.holder.turtleSerializers.forEach {
            val upgrade = ComputerPlatformToolkit.get().getTurtleUpgrade(ComputerPlatformRegistries.TURTLE_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                ComputerPlatformToolkit.get().createTurtlesWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketSerializers.forEach {
            val upgrade = ComputerPlatformToolkit.get().getPocketUpgrade(ComputerPlatformRegistries.POCKET_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                ComputerPlatformToolkit.get().createPocketsWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
    }
}
