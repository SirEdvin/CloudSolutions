package site.siredvin.cloudsolutions.common.setup

import site.siredvin.cloudsolutions.computercraft.peripheral.CrafkaBrokerPeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import site.siredvin.cloudsolutions.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.PocketPeripheralOwner
import site.siredvin.tweakium.modules.pocket.PeripheralPocketUpgrade

object PocketUpgradeSerializers {

    val STATSD_BRIDGE = ModPlatform.registerPocketUpgradeWithSelfCustomItem(
        StatsDBridgePeripheral.ID,
    ) { id, type, stack ->
        PeripheralPocketUpgrade(
            id,
            stack,
            { StatsDBridgePeripheral(PocketPeripheralOwner(it)) },
            { type },
        )
    }

    val KV_STORAGE = ModPlatform.registerPocketUpgradeWithSelfCustomItem(
        KVStoragePeripheral.ID,
    ) { id, type, stack ->
        PeripheralPocketUpgrade(
            id,
            stack,
            { KVStoragePeripheral(PocketPeripheralOwner(it)) },
            { type },
        )
    }

    val CRAFKA_BROKER = ModPlatform.registerPocketUpgradeWithSelfCustomItem(
        CrafkaBrokerPeripheral.ID,
    ) { id, type, stack ->
        PeripheralPocketUpgrade(
            id,
            stack,
            { CrafkaBrokerPeripheral(PocketPeripheralOwner(it)) },
            { type },
        )
    }

    fun doSomething() {}
}
