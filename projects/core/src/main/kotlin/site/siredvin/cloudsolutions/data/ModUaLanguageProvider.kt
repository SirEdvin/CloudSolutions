package site.siredvin.cloudsolutions.data

import net.minecraft.data.PackOutput
import site.siredvin.cloudsolutions.common.setup.Blocks
import java.util.function.Consumer

class ModUaLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "uk_ua") {

    companion object {
        private val hooks: MutableList<Consumer<ModUaLanguageProvider>> = mutableListOf()

        fun addHook(hook: Consumer<ModUaLanguageProvider>) {
            hooks.add(hook)
        }
    }

    override fun addTranslations() {
        add(ModText.CREATIVE_TAB, "Цитадель даних")
        add(Blocks.KV_STORAGE.get(), "База даних «ключ—значення»", "Наразі не використовується")
        add(Blocks.TSDB_STORAGE.get(), "База даних часових рядів", "Наразі не використовується")
        add(Blocks.STATSD_BRIDGE.get(), "StatsD міст", "Дозволяє відправляти statsd метрики на якийсь statsd сервер вказаний в налаштуваннях")
        add(ModText.UNFINISHED_AND_DISABLED, "  §4Цей предмет не готовий та був відключений. Просто ігноруйте його")
        hooks.forEach { it.accept(this) }
    }
}
