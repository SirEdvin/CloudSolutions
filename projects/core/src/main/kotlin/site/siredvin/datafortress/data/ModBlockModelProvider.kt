package site.siredvin.datafortress.data

import net.minecraft.data.models.BlockModelGenerators
import site.siredvin.datafortress.common.setup.Blocks
import site.siredvin.peripheralium.data.blocks.horizontalOrientatedBlock

object ModBlockModelProvider {

    @Suppress("UNUSED_PARAMETER")
    fun addModels(generators: BlockModelGenerators) {
        horizontalOrientatedBlock(
            generators, Blocks.DATA_STORAGE.get(),
        )
    }
}
