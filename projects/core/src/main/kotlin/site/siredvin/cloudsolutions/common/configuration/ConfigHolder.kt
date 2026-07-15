package site.siredvin.cloudsolutions.common.configuration

import net.neoforged.neoforge.common.ModConfigSpec

object ConfigHolder {
    var commonSpec: ModConfigSpec
    var commonConfig: ModConfig.CommonConfig
    var serverSpec: ModConfigSpec
    var serverConfig: ModConfig.ServerConfig

    init {
        val (key, value) = ModConfigSpec.Builder()
            .configure { builder: ModConfigSpec.Builder -> ModConfig.CommonConfig(builder) }
        commonConfig = key
        commonSpec = value

        val (serverKey, serverValue) = ModConfigSpec.Builder()
            .configure { builder: ModConfigSpec.Builder -> ModConfig.ServerConfig(builder) }
        serverConfig = serverKey
        serverSpec = serverValue
    }
}
