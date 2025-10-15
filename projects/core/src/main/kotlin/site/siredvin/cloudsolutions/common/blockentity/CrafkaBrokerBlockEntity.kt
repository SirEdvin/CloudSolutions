package site.siredvin.cloudsolutions.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.cloudsolutions.common.setup.ModBlockEntityTypes
import site.siredvin.cloudsolutions.computercraft.peripheral.CrafkaBrokerPeripheral
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class CrafkaBrokerBlockEntity(blockPos: BlockPos, blockState: BlockState) : PeripheralBlockEntity<CrafkaBrokerPeripheral>(ModBlockEntityTypes.CRAFKA_BROKER.get(), blockPos, blockState) {

    override fun createPeripheral(side: Direction): CrafkaBrokerPeripheral = CrafkaBrokerPeripheral(BlockEntityPeripheralOwner(this))
}
