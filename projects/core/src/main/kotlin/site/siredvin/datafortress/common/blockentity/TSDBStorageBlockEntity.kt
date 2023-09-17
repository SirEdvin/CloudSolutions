package site.siredvin.datafortress.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.datafortress.common.setup.BlockEntityTypes
import site.siredvin.datafortress.computercraft.peripheral.TSDBStoragePeripheral
import site.siredvin.peripheralium.common.blockentities.PeripheralBlockEntity
import site.siredvin.peripheralium.computercraft.peripheral.owner.BlockEntityPeripheralOwner

class TSDBStorageBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    PeripheralBlockEntity<TSDBStoragePeripheral>(BlockEntityTypes.TSDB_STORAGE.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): TSDBStoragePeripheral {
        return TSDBStoragePeripheral(BlockEntityPeripheralOwner(this))
    }
}
