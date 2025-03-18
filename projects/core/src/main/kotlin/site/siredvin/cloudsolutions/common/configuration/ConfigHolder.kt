package site.siredvin.cloudsolutions.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ConfigHolder {
    var commonSpec: ForgeConfigSpec
    var commonConfig: ModConfig.CommonConfig
    var serverSpec: ForgeConfigSpec
    var serverConfig: ModConfig.ServerConfig

    init {
        val (key, value) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> ModConfig.CommonConfig(builder) }
        commonConfig = key
        commonSpec = value

        val (serverKey, serverValue) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> ModConfig.ServerConfig(builder) }
        serverConfig = serverKey
        serverSpec = serverValue
    }
}
