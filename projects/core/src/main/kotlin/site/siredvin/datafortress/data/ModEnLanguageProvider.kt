package site.siredvin.datafortress.data

import net.minecraft.data.PackOutput
import site.siredvin.datafortress.common.setup.Blocks
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
        add(Blocks.DATA_STORAGE.get(), "Data storage", "Have no uses right now")
        add(Blocks.TSDB_STORAGE.get(), "Timeseries database", "Have no uses right now")
        add(Blocks.STATSD_BRIDGE.get(), "StatsD bridge", "Allows you to send metrics to preconfigured statsd server")
        add(ModText.UNFINISHED_AND_DISABLED, "  §4This item is not ready and disabled for now. Just ignore it")
        hooks.forEach { it.accept(this) }
    }
}
