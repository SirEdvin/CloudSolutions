package site.siredvin.cloudsolutions.fabric

import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.peripheralium.fabric.FabricBaseInnerPlatform

object FabricModPlatform : FabricBaseInnerPlatform() {
    override val modID: String
        get() = CloudSolutionsCore.MOD_ID
}
