package site.siredvin.cloudsolutions.common.setup

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import site.siredvin.cloudsolutions.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner
import site.siredvin.tweakium.modules.turtle.PeripheralTurtleUpgrade

object TurtleUpgradeSerializers {

    val STATSD_BRIDGE = ModPlatform.registerTurtleUpgrade(
        StatsDBridgePeripheral.ID,
        TurtleUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralTurtleUpgrade.dynamic(
                stack.item,
                { turtle: ITurtleAccess, side: TurtleSide -> StatsDBridgePeripheral(TurtlePeripheralOwner(turtle, side)) },
                { StatsDBridgePeripheral.ID },
            )
        },
    )

    val KV_STORAGE = ModPlatform.registerTurtleUpgrade(
        KVStoragePeripheral.ID,
        TurtleUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralTurtleUpgrade.dynamic(
                stack.item,
                { turtle: ITurtleAccess, side: TurtleSide -> KVStoragePeripheral(TurtlePeripheralOwner(turtle, side)) },
                { KVStoragePeripheral.ID },
            )
        },
    )

    fun doSomething() {}
}
