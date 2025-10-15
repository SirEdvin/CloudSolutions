package site.siredvin.cloudsolutions.data

import net.minecraft.data.PackOutput
import site.siredvin.cloudsolutions.common.setup.ModBlocks
import site.siredvin.cloudsolutions.computercraft.peripheral.CrafkaBrokerPeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.KVStoragePeripheral
import site.siredvin.cloudsolutions.computercraft.peripheral.StatsDBridgePeripheral
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
        add(ModBlocks.KV_STORAGE.get(), "База даних «ключ—значення»", "Наразі не використовується")
        add(ModBlocks.TSDB_STORAGE.get(), "База даних часових рядів", "Наразі не використовується")
        add(ModBlocks.STATSD_BRIDGE.get(), "StatsD міст", "Дозволяє відправляти statsd метрики на якийсь statsd сервер вказаний в налаштуваннях")
        add(ModBlocks.CRAFKA_BROKER.get(), "Crafka брокер", "Це як той відомий брокер Kafka, але з величезною кількістю крафта всередині")
        add(ModText.UNFINISHED_AND_DISABLED, "  §4Цей предмет не готовий та був відключений. Просто ігноруйте його")

        addTurtle(KVStoragePeripheral.ID, "Ключ-значення зберігаюча")
        addTurtle(StatsDBridgePeripheral.ID, "StatsD прокидуюча")
        addTurtle(CrafkaBrokerPeripheral.ID, "Крафкадотична")
        addPocket(KVStoragePeripheral.ID, "Ключ-значення зберігаючий")
        addPocket(StatsDBridgePeripheral.ID, "StatsD прокидуючий")
        addPocket(CrafkaBrokerPeripheral.ID, "Крафкрадотичний")
        hooks.forEach { it.accept(this) }
    }
}
