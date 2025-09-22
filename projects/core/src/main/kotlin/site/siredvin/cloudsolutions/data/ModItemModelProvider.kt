package site.siredvin.cloudsolutions.data

import net.minecraft.data.models.ItemModelGenerators
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.tweakium.modules.data.turtleUpgrades

object ModItemModelProvider {

    fun addModels(@Suppress("UNUSED_PARAMETER") generators: ItemModelGenerators) {
        turtleUpgrades(generators, ModBlocks.KV_STORAGE.get())
        turtleUpgrades(generators, ModBlocks.STATSD_BRIDGE.get())
    }
}
