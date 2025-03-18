package site.siredvin.cloudsolutions.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.cloudsolutions.common.setup.BlockEntityTypes
import site.siredvin.cloudsolutions.computercraft.peripheral.TSDBStoragePeripheral
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class TSDBStorageBlockEntity(blockPos: BlockPos, blockState: BlockState) : PeripheralBlockEntity<TSDBStoragePeripheral>(BlockEntityTypes.TSDB_STORAGE.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): TSDBStoragePeripheral = TSDBStoragePeripheral(BlockEntityPeripheralOwner(this))
}
