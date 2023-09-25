package site.siredvin.cloudsolutions.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.cloudsolutions.common.setup.BlockEntityTypes
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import site.siredvin.peripheralium.common.blockentities.PeripheralBlockEntity
import site.siredvin.peripheralium.computercraft.peripheral.owner.BlockEntityPeripheralOwner

class StatsDBridgeBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    PeripheralBlockEntity<StatsDBridgePeripheral>(BlockEntityTypes.STATSD_BRIDGE.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): StatsDBridgePeripheral {
        return StatsDBridgePeripheral(BlockEntityPeripheralOwner(this))
    }
}
