package site.siredvin.datafortress.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.datafortress.common.setup.BlockEntityTypes
import site.siredvin.datafortress.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.peripheralium.common.blockentities.PeripheralBlockEntity
import site.siredvin.peripheralium.computercraft.peripheral.owner.BlockEntityPeripheralOwner

class KVStorageBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    PeripheralBlockEntity<KVStoragePeripheral>(BlockEntityTypes.KV_STORAGE.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): KVStoragePeripheral {
        return KVStoragePeripheral(BlockEntityPeripheralOwner(this))
    }
}
