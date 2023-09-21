package site.siredvin.datafortress.data

import site.siredvin.datafortress.DataFortressCore
import site.siredvin.peripheralium.data.language.TextRecord

enum class ModText : TextRecord {
    CREATIVE_TAB,
    UNFINISHED_AND_DISABLED,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", DataFortressCore.MOD_ID, name.lowercase())
    }
}
