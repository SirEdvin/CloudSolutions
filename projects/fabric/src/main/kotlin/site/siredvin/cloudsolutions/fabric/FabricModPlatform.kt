package site.siredvin.cloudsolutions.fabric

import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.tweakium.modules.platform.FabricInnerComputerBasePlatform

object FabricModPlatform : FabricInnerComputerBasePlatform() {
    override val modID: String
        get() = CloudSolutionsCore.MOD_ID
}
