package site.siredvin.cloudsolutions.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.cloudsolutions.common.setup.ModBlockEntityTypes
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class StatsDBridgeBlockEntity(blockPos: BlockPos, blockState: BlockState) : PeripheralBlockEntity<StatsDBridgePeripheral>(ModBlockEntityTypes.STATSD_BRIDGE.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): StatsDBridgePeripheral = StatsDBridgePeripheral(BlockEntityPeripheralOwner(this))
}
