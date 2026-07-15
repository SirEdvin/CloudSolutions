package site.siredvin.cloudsolutions.common.setup

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import site.siredvin.cloudsolutions.computercraft.peripheral.CrafkaBrokerPeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import site.siredvin.cloudsolutions.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner
import site.siredvin.tweakium.modules.turtle.PeripheralTurtleUpgrade

object TurtleUpgradeSerializers {

    val STATSD_BRIDGE = ModPlatform.registerTurtleUpgradeWithSelfCustomItem(
        StatsDBridgePeripheral.ID,
    ) { id, type, stack ->
        PeripheralTurtleUpgrade.dynamic(
            stack.item,
            { turtle: ITurtleAccess, side: TurtleSide -> StatsDBridgePeripheral(TurtlePeripheralOwner(turtle, side)) },
            { type },
            { id },
        )
    }

    val KV_STORAGE = ModPlatform.registerTurtleUpgradeWithSelfCustomItem(
        KVStoragePeripheral.ID,
    ) { id, type, stack ->
        PeripheralTurtleUpgrade.dynamic(
            stack.item,
            { turtle: ITurtleAccess, side: TurtleSide -> KVStoragePeripheral(TurtlePeripheralOwner(turtle, side)) },
            { type },
            { id },
        )
    }

    val CRAFKA_BROKER = ModPlatform.registerTurtleUpgradeWithSelfCustomItem(
        CrafkaBrokerPeripheral.ID,
    ) { id, type, stack ->
        PeripheralTurtleUpgrade.dynamic(
            stack.item,
            { turtle: ITurtleAccess, side: TurtleSide -> CrafkaBrokerPeripheral(TurtlePeripheralOwner(turtle, side)) },
            { type },
            { id },
        )
    }

    fun doSomething() {}
}
