package site.siredvin.cloudsolutions.data

import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.resources.ResourceLocation
import site.siredvin.broccolium.modules.data.model.horizontalOrientatedBlock
import site.siredvin.broccolium.modules.data.model.horizontalOrientedModel
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.setup.ModBlocks

object ModBlockModelProvider {
    fun addModels(generators: BlockModelGenerators) {
        val peripheralCasingTexture = ResourceLocation(CloudSolutionsCore.MOD_ID, "block/peripheral_casing")
        horizontalOrientatedBlock(
            generators,
            ModBlocks.KV_STORAGE.get(),
        )
        horizontalOrientatedBlock(
            generators,
            ModBlocks.TSDB_STORAGE.get(),
            horizontalOrientedModel(
                generators,
                ModBlocks.TSDB_STORAGE.get(),
                overwriteTop = peripheralCasingTexture,
                overwriteFront = peripheralCasingTexture,
                overwriteBottom = peripheralCasingTexture,
                overwriteSide = peripheralCasingTexture,
            ),
        )

        horizontalOrientatedBlock(
            generators,
            ModBlocks.STATSD_BRIDGE.get(),
            horizontalOrientedModel(
                generators,
                ModBlocks.STATSD_BRIDGE.get(),
                overwriteFront = ResourceLocation(CloudSolutionsCore.MOD_ID, "block/statsd_bridge"),
                overwriteSide = peripheralCasingTexture,
                overwriteBottom = peripheralCasingTexture,
                overwriteTop = peripheralCasingTexture,
            ),
        )
    }
}
