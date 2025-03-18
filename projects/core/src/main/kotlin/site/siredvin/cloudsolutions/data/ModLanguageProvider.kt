package site.siredvin.cloudsolutions.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.lang.LanguageProvider
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.xplat.ModPlatform
import java.util.stream.Stream

abstract class ModLanguageProvider(output: PackOutput, locale: String) :
    LanguageProvider(
        output,
        CloudSolutionsCore.MOD_ID,
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

    override fun getExpectedKeys(): Stream<String> = Stream.concat(super.getExpectedKeys(), extraExpectedKeys.stream())
}
