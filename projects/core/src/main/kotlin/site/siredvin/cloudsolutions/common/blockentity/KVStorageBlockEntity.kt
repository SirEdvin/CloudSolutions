package site.siredvin.cloudsolutions.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.cloudsolutions.common.setup.ModBlockEntityTypes
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class KVStorageBlockEntity(blockPos: BlockPos, blockState: BlockState) : PeripheralBlockEntity<KVStoragePeripheral>(ModBlockEntityTypes.KV_STORAGE.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): KVStoragePeripheral = KVStoragePeripheral(BlockEntityPeripheralOwner(this))
}
