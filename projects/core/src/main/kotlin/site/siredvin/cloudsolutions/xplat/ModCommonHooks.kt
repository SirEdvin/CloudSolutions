package site.siredvin.cloudsolutions.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
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
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit

object ModCommonHooks {

    fun onRegister() {
        ModItems.doSomething()
        ModBlocks.doSomething()
        ModBlockEntityTypes.doSomething()
        PocketUpgradeSerializers.doSomething()
        TurtleUpgradeSerializers.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation.fromNamespaceAndPath(CloudSolutionsCore.MOD_ID, "tab"),
            CloudSolutionsCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }

    fun onServerStarted(server: MinecraftServer) {
        SubsystemManager.onServerStart(server)
    }

    fun onServerStopping(server: MinecraftServer) {
        SubsystemManager.onServerStop(server)
    }

    fun registerUpgradesInCreativeTab(holders: HolderLookup.Provider, output: CreativeModeTab.Output) {
        ModPlatform.holder.turtleUpgrades.forEach {
            holders.lookupOrThrow(ITurtleUpgrade.REGISTRY).get(ResourceKey.create(ITurtleUpgrade.REGISTRY, it.id)).ifPresent { upgrade ->
                ComputerPlatformToolkit.get().createTurtlesWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketUpgrades.forEach {
            holders.lookupOrThrow(IPocketUpgrade.REGISTRY).get(ResourceKey.create(IPocketUpgrade.REGISTRY, it.id)).ifPresent { upgrade ->
                ComputerPlatformToolkit.get().createPocketsWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
    }
}
