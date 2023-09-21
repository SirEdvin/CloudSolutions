package site.siredvin.datafortress.xplat

import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.common.configuration.ModConfig
import site.siredvin.datafortress.common.setup.BlockEntityTypes
import site.siredvin.datafortress.common.setup.Blocks
import site.siredvin.datafortress.common.setup.Items
import site.siredvin.datafortress.subsystems.statsq.StatsDClient
import site.siredvin.datafortress.subsystems.webserver.WebserverMain
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries

object ModCommonHooks {

    fun onRegister() {
        Items.doSomething()
        Blocks.doSomething()
        BlockEntityTypes.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation(DataFortressCore.MOD_ID, "tab"),
            DataFortressCore.configureCreativeTab(PeripheraliumPlatform.createTabBuilder()).build(),
        )
    }

    fun onServerStarted() {
        if (ModConfig.enableInternalWebserver) {
            val webserverThread = Thread({ WebserverMain.main(7000) }, "dataFortress-webserver")
            webserverThread.isDaemon = true
            webserverThread.start()
        }
        if (ModConfig.enableStatsDBridge) {
            StatsDClient.init()
        }
    }

    fun registerUpgradesInCreativeTab(output: CreativeModeTab.Output) {
        ModPlatform.holder.turtleSerializers.forEach {
            val upgrade = PeripheraliumPlatform.getTurtleUpgrade(XplatRegistries.TURTLE_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                PeripheraliumPlatform.createTurtlesWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketSerializers.forEach {
            val upgrade = PeripheraliumPlatform.getPocketUpgrade(XplatRegistries.POCKET_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                PeripheraliumPlatform.createPocketsWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
    }
}
