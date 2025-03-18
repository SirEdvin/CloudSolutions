package site.siredvin.cloudsolutions.data

import site.siredvin.broccolium.modules.data.api.TextRecord
import site.siredvin.cloudsolutions.CloudSolutionsCore

enum class ModText : TextRecord {
    CREATIVE_TAB,
    UNFINISHED_AND_DISABLED,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", CloudSolutionsCore.MOD_ID, name.lowercase())
    }
}
