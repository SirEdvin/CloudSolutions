package site.siredvin.datafortress.data

import net.minecraft.data.PackOutput
import site.siredvin.datafortress.common.setup.Blocks
import site.siredvin.datafortress.common.setup.Items
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
        add(Items.TEMPLATE_ITEM.get(), "Шаблоний предмет", "Боже, у нього навіть нормальної текстури немає")
        add(ModText.CREATIVE_TAB, "А це треба перейменувати, друже")
        add(Blocks.DATA_STORAGE.get(), "Data storage", "Something very useful")
        add(Blocks.TSDB_STORAGE.get(), "Timeseries database", "Something very useful")
        hooks.forEach { it.accept(this) }
    }
}
