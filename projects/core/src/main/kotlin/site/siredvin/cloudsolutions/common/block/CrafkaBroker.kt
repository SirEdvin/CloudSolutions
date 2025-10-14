package site.siredvin.cloudsolutions.common.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.cloudsolutions.common.blockentity.CrafkaBrokerBlockEntity
import site.siredvin.cloudsolutions.common.setup.ModBlockEntityTypes
import java.util.stream.Stream

class CrafkaBroker: FacingBlockEntityBlock<CrafkaBrokerBlockEntity>({ ModBlockEntityTypes.CRAFKA_BROKER.get() }, true) {
    companion object {
        val SHAPE = Stream.of(
            Shapes.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0),
            Shapes.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Shapes.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0)
        ).reduce { v1: VoxelShape, v2: VoxelShape -> Shapes.join(v1, v2, BooleanOp.OR) }.get()

    }

    @Deprecated("Deprecated in Java")
    override fun getShape(state: BlockState, blockGetter: BlockGetter, blockPos: BlockPos, collisionContext: CollisionContext,): VoxelShape {
        return SHAPE
    }
}