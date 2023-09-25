package site.siredvin.cloudsolutions.data

import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.resources.ResourceLocation
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.setup.Blocks
import site.siredvin.peripheralium.data.blocks.horizontalOrientatedBlock
import site.siredvin.peripheralium.data.blocks.horizontalOrientedModel

object ModBlockModelProvider {
    fun addModels(generators: BlockModelGenerators) {
        val peripheralCasingTexture = ResourceLocation(CloudSolutionsCore.MOD_ID, "block/peripheral_casing")
        horizontalOrientatedBlock(
            generators,
            Blocks.KV_STORAGE.get(),
        )
        horizontalOrientatedBlock(
            generators,
            Blocks.TSDB_STORAGE.get(),
            horizontalOrientedModel(
                generators,
                Blocks.TSDB_STORAGE.get(),
                overwriteTop = peripheralCasingTexture,
                overwriteFront = peripheralCasingTexture,
                overwriteBottom = peripheralCasingTexture,
                overwriteSide = peripheralCasingTexture,
            ),
        )

        horizontalOrientatedBlock(
            generators,
            Blocks.STATSD_BRIDGE.get(),
            horizontalOrientedModel(
                generators,
                Blocks.STATSD_BRIDGE.get(),
                overwriteFront = ResourceLocation(CloudSolutionsCore.MOD_ID, "block/statsd_bridge"),
                overwriteSide = peripheralCasingTexture,
                overwriteBottom = peripheralCasingTexture,
                overwriteTop = peripheralCasingTexture,
            ),
        )
    }
}
