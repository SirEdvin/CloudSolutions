package site.siredvin.cloudsolutions.data

import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.peripheralium.data.language.TextRecord

enum class ModText : TextRecord {
    CREATIVE_TAB,
    UNFINISHED_AND_DISABLED,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", CloudSolutionsCore.MOD_ID, name.lowercase())
    }
}
