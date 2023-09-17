package site.siredvin.datafortress.data

import net.minecraft.data.PackOutput
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.xplat.ModPlatform
import site.siredvin.peripheralium.data.language.LanguageProvider
import java.util.stream.Stream

abstract class ModLanguageProvider(output: PackOutput, locale: String) : LanguageProvider(
    output,
    DataFortressCore.MOD_ID,
    locale,
    ModPlatform.holder,
    *ModText.values(),
) {

    companion object {
        private val extraExpectedKeys: MutableList<String> = mutableListOf()

        fun addExpectedKey(key: String) {
            extraExpectedKeys.add(key)
        }
    }

    override fun getExpectedKeys(): Stream<String> {
        return Stream.concat(super.getExpectedKeys(), extraExpectedKeys.stream())
    }
}
