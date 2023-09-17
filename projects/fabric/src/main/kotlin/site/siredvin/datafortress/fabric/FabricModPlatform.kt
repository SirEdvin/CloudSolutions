package site.siredvin.datafortress.fabric

import site.siredvin.datafortress.DataFortressCore
import site.siredvin.peripheralium.fabric.FabricBaseInnerPlatform

object FabricModPlatform : FabricBaseInnerPlatform() {
    override val modID: String
        get() = DataFortressCore.MOD_ID
}
