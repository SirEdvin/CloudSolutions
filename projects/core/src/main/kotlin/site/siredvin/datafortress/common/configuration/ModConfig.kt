package site.siredvin.datafortress.common.configuration

import net.minecraftforge.common.ForgeConfigSpec
import site.siredvin.peripheralium.api.config.IConfigHandler

object ModConfig {

    val enableInternalWebserver: Boolean
        // This is totally unfinished work, so we enable this only locally
        get() = false

    val enableTSDBStorage: Boolean
        get() = false

    val enableDataStorage: Boolean
        get() = false

    val enableStatsDBridge: Boolean
        get() = ConfigHolder.COMMON_CONFIG.ENABLE_STATSD_BRIDGE.get()

    val statsdPort: Int
        get() = ConfigHolder.COMMON_CONFIG.STATSD_PORT.get()

    val statsdHostname: String
        get() = ConfigHolder.COMMON_CONFIG.STATSD_HOSTNAME.get()
    val statsdPrefix: String
        get() = ConfigHolder.COMMON_CONFIG.STATSD_PREFIX.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // Generic plugins
        val ENABLE_STATSD_BRIDGE: ForgeConfigSpec.BooleanValue
        val STATSD_PORT: ForgeConfigSpec.IntValue
        val STATSD_HOSTNAME: ForgeConfigSpec.ConfigValue<String>
        val STATSD_PREFIX: ForgeConfigSpec.ConfigValue<String>

        // TODO: This should be server config

        init {
            builder.push("statsd")
            ENABLE_STATSD_BRIDGE = builder.comment("Enables statsd bridge")
                .define("enableStatsDBridge", false)
            STATSD_PORT = builder.comment("StatsD port")
                .defineInRange("statsdPort", 8125, 1, 65555)
            STATSD_HOSTNAME = builder.comment("StatsD hostname")
                .define("statsdHostname", "127.0.0.1")
            STATSD_PREFIX = builder.comment("StatsD prefix")
                .define("statsdPrefix", "")
            builder.pop()
        }

        private fun register(data: Array<out IConfigHandler>, builder: ForgeConfigSpec.Builder) {
            for (handler in data) {
                handler.addToConfig(builder)
            }
        }
    }
}
