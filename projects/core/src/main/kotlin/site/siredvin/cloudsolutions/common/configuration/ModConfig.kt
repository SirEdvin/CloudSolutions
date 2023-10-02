package site.siredvin.cloudsolutions.common.configuration

import net.minecraftforge.common.ForgeConfigSpec
import site.siredvin.cloudsolutions.subsystems.KVStorageMode
import site.siredvin.peripheralium.api.config.IConfigHandler

object ModConfig {

    val enableInternalWebserver: Boolean
        // This is totally unfinished work, so we enable this only locally
        get() = false

    val enableTSDBStorage: Boolean
        get() = false

    val enableKVStorage: Boolean
        get() = ConfigHolder.COMMON_CONFIG.ENABLE_KV_STORAGE.get()

    val enableStatsDBridge: Boolean
        get() = ConfigHolder.COMMON_CONFIG.ENABLE_STATSD_BRIDGE.get()

    val enableStatsDConnection: Boolean
        get() = ConfigHolder.SERVER_CONFIG.ENABLE_STATSD_CONNECTION.get()

    val statsdPort: Int
        get() = ConfigHolder.SERVER_CONFIG.STATSD_PORT.get()

    val statsdHostname: String
        get() = ConfigHolder.SERVER_CONFIG.STATSD_HOSTNAME.get()
    val statsdPrefix: String
        get() = ConfigHolder.SERVER_CONFIG.STATSD_PREFIX.get()

    val statsdPlayerRateLimit: Int
        get() = ConfigHolder.SERVER_CONFIG.STATSD_PLAYER_RATE_LIMIT.get()

    val statsdGlobalRateLimit: Int
        get() = ConfigHolder.SERVER_CONFIG.STATSD_GLOBAL_RATE_LIMIT.get()

    val kvStorageMode: KVStorageMode
        get() {
            return try {
                KVStorageMode.valueOf(ConfigHolder.SERVER_CONFIG.KV_STORAGE_MODE.get().uppercase())
            } catch (e: IllegalArgumentException) {
                KVStorageMode.DISABLED
            }
        }

    val kvStorageKeyLimit: Int
        get() = ConfigHolder.SERVER_CONFIG.KV_STORAGE_KEY_LIMIT.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // Generic plugins
        val ENABLE_STATSD_BRIDGE: ForgeConfigSpec.BooleanValue
        val ENABLE_KV_STORAGE: ForgeConfigSpec.BooleanValue

        init {
            builder.push("statsd")
            ENABLE_STATSD_BRIDGE = builder.comment("Enables statsd bridge")
                .define("enableStatsDBridge", true)
            ENABLE_KV_STORAGE = builder.comment("Enables KV storage")
                .define("enableKVStorage", true)
            builder.pop()
        }

        private fun register(data: Array<out IConfigHandler>, builder: ForgeConfigSpec.Builder) {
            for (handler in data) {
                handler.addToConfig(builder)
            }
        }
    }

    class ServerConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // StatsD
        val ENABLE_STATSD_CONNECTION: ForgeConfigSpec.BooleanValue
        val STATSD_PORT: ForgeConfigSpec.IntValue
        val STATSD_HOSTNAME: ForgeConfigSpec.ConfigValue<String>
        val STATSD_PREFIX: ForgeConfigSpec.ConfigValue<String>
        val STATSD_PLAYER_RATE_LIMIT: ForgeConfigSpec.IntValue
        val STATSD_GLOBAL_RATE_LIMIT: ForgeConfigSpec.IntValue

        // Data storage
        val KV_STORAGE_MODE: ForgeConfigSpec.ConfigValue<String>
        val KV_STORAGE_KEY_LIMIT: ForgeConfigSpec.IntValue

        init {
            builder.push("statsd")
            ENABLE_STATSD_CONNECTION = builder.comment("StatsD connection")
                .define("enableStatsDConnection", false)
            STATSD_PORT = builder.comment("StatsD port")
                .defineInRange("statsdPort", 8125, 1, 65555)
            STATSD_HOSTNAME = builder.comment("StatsD hostname")
                .define("statsdHostname", "127.0.0.1")
            STATSD_PREFIX = builder.comment("StatsD prefix")
                .define("statsdPrefix", "")
            STATSD_PLAYER_RATE_LIMIT = builder.comment("StatsD rate limit per player in event per minute")
                .defineInRange("statsdPlayerRateLimit", 1000, 1, Int.MAX_VALUE)
            STATSD_GLOBAL_RATE_LIMIT = builder.comment("StatsD global rate limit in event per minute")
                .defineInRange("statsdGlobalRateLimit", 1000, 1, Int.MAX_VALUE)
            builder.pop()
            builder.push("kv")
            KV_STORAGE_MODE = builder.comment("Mode of KV storage")
                .define("kvStorageMode", KVStorageMode.SQLITE.name) {
                    if (it == null) return@define false
                    return@define try {
                        KVStorageMode.valueOf(it.toString().uppercase())
                        true
                    } catch (e: IllegalArgumentException) {
                        false
                    }
                }
            KV_STORAGE_KEY_LIMIT = builder.comment("Limit for active keys in storage per player")
                .defineInRange("kvStorageKeyLimit", 21_000, 1, Int.MAX_VALUE)
            builder.pop()
        }

        private fun register(data: Array<out IConfigHandler>, builder: ForgeConfigSpec.Builder) {
            for (handler in data) {
                handler.addToConfig(builder)
            }
        }
    }
}
