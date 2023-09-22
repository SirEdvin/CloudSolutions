package site.siredvin.datafortress.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ConfigHolder {
    var COMMON_SPEC: ForgeConfigSpec
    var COMMON_CONFIG: ModConfig.CommonConfig
    var SERVER_SPEC: ForgeConfigSpec
    var SERVER_CONFIG: ModConfig.ServerConfig

    init {
        val (key, value) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> ModConfig.CommonConfig(builder) }
        COMMON_CONFIG = key
        COMMON_SPEC = value

        val (serverKey, serverValue) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> ModConfig.ServerConfig(builder) }
        SERVER_CONFIG = serverKey
        SERVER_SPEC = serverValue
    }
}
