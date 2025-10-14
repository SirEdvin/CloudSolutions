package site.siredvin.cloudsolutions.data

import net.minecraft.data.PackOutput
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
import java.util.function.Consumer

class ModEnLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "en_us") {

    companion object {
        private val hooks: MutableList<Consumer<ModEnLanguageProvider>> = mutableListOf()

        fun addHook(hook: Consumer<ModEnLanguageProvider>) {
            hooks.add(hook)
        }
    }

    override fun addTranslations() {
        add(ModText.CREATIVE_TAB, "Data fortress")
        add(ModBlocks.KV_STORAGE.get(), "KV storage", "Have no uses right now")
        add(ModBlocks.TSDB_STORAGE.get(), "Timeseries database", "Have no uses right now")
        add(ModBlocks.STATSD_BRIDGE.get(), "StatsD bridge", "Allows you to send metrics to preconfigured statsd server")
        add(ModBlocks.CRAFKA_BROKER.get(), "Crafka broker", "It is like that Kafka broker, but with so much craft inside!")
        add(ModText.UNFINISHED_AND_DISABLED, "  §4This item is not ready and disabled for now. Just ignore it")

        addUpgrades(KVStoragePeripheral.ID, "KV Storing")
        addUpgrades(StatsDBridgePeripheral.ID, "StatsD bridging")
        hooks.forEach { it.accept(this) }
    }
}
