package site.siredvin.cloudsolutions.common.setup

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import site.siredvin.cloudsolutions.computercraft.peripheral.CrafkaBrokerPeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import site.siredvin.cloudsolutions.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.PocketPeripheralOwner
import site.siredvin.tweakium.modules.pocket.PeripheralPocketUpgrade

object PocketUpgradeSerializers {

    val STATSD_BRIDGE = ModPlatform.registerPocketUpgrade(
        StatsDBridgePeripheral.ID,
        PocketUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralPocketUpgrade(
                id,
                stack,
                { StatsDBridgePeripheral(PocketPeripheralOwner(it)) },
            )
        },
    )

    val KV_STORAGE = ModPlatform.registerPocketUpgrade(
        KVStoragePeripheral.ID,
        PocketUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralPocketUpgrade(
                id,
                stack,
                { KVStoragePeripheral(PocketPeripheralOwner(it)) },
            )
        },
    )

    val CRAFKA_BROKER = ModPlatform.registerPocketUpgrade(
        CrafkaBrokerPeripheral.ID,
        PocketUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralPocketUpgrade(
                id,
                stack,
                { CrafkaBrokerPeripheral(PocketPeripheralOwner(it)) },
            )
        },
    )

    fun doSomething() {}
}
