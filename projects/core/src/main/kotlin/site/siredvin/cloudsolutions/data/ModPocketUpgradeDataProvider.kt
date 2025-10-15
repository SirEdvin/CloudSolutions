package site.siredvin.cloudsolutions.data

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import net.minecraft.data.PackOutput
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.common.setup.PocketUpgradeSerializers
import site.siredvin.cloudsolutions.xplat.ModPlatform
import site.siredvin.tweakium.modules.data.LibPocketUpgradeDataProvider
import java.util.function.Consumer
import java.util.function.Function

class ModPocketUpgradeDataProvider(output: PackOutput) : LibPocketUpgradeDataProvider(output, ModPlatform.holder.pocketSerializers) {
    companion object {
        private val REGISTERED_BUILDERS: MutableList<Function<PocketUpgradeDataProvider, Upgrade<PocketUpgradeSerialiser<*>>>> = mutableListOf()

        fun hookUpgrade(builder: Function<PocketUpgradeDataProvider, Upgrade<PocketUpgradeSerialiser<*>>>) {
            REGISTERED_BUILDERS.add(builder)
        }
    }

    override fun registerUpgrades(addUpgrade: Consumer<Upgrade<PocketUpgradeSerialiser<*>>>) {
        REGISTERED_BUILDERS.forEach {
            it.apply(this).add(addUpgrade)
        }
        addUpgrade.accept(simpleWithCustomItem(PocketUpgradeSerializers.STATSD_BRIDGE, ModBlocks.STATSD_BRIDGE))
        addUpgrade.accept(simpleWithCustomItem(PocketUpgradeSerializers.KV_STORAGE, ModBlocks.KV_STORAGE))
        addUpgrade.accept(simpleWithCustomItem(PocketUpgradeSerializers.CRAFKA_BROKER, ModBlocks.CRAFKA_BROKER))
    }
}
