package site.siredvin.cloudsolutions.common.configuration

import net.minecraftforge.common.ForgeConfigSpec
import site.siredvin.cloudsolutions.subsystems.CrafkaStorageMode
import site.siredvin.cloudsolutions.subsystems.KVStorageMode

object ModConfig {

    val enableInternalWebserver: Boolean
        // This is totally unfinished work, so we enable this only locally
        get() = false

    val enableTSDBStorage: Boolean
        get() = false

    val enableKVStorage: Boolean
        get() = ConfigHolder.commonConfig.enableKVBridge.get()

    val enableCrafkaBroker: Boolean
        get() = ConfigHolder.commonConfig.enableCrafkaBroker.get()

    val enableStatsDBridge: Boolean
        get() = ConfigHolder.commonConfig.enableStatsDBridge.get()

    val enableStatsDConnection: Boolean
        get() = ConfigHolder.serverConfig.enableStatsDConnection.get()

    val statsdPort: Int
        get() = ConfigHolder.serverConfig.statsDPort.get()

    val statsdHostname: String
        get() = ConfigHolder.serverConfig.statsDHostName.get()
    val statsdPrefix: String
        get() = ConfigHolder.serverConfig.statsDPrefix.get()

    val statsdPlayerRateLimit: Int
        get() = ConfigHolder.serverConfig.statsDPlayerRateLimit.get()

    val statsdGlobalRateLimit: Int
        get() = ConfigHolder.serverConfig.statsDGlobalRateLimit.get()

    val kvStorageMode: KVStorageMode
        get() {
            return try {
                KVStorageMode.valueOf(ConfigHolder.serverConfig.kvStorageMode.get().uppercase())
            } catch (e: IllegalArgumentException) {
                KVStorageMode.DISABLED
            }
        }

    val kvStorageKeyLimit: Int
        get() = ConfigHolder.serverConfig.kvStorageKeyLimit.get()

    val kvStorageValueLimit: Int
        get() = ConfigHolder.serverConfig.kvStorageValueLimit.get()

    val crafkaStorageMode: CrafkaStorageMode
        get() {
            return try {
                CrafkaStorageMode.valueOf(ConfigHolder.serverConfig.crafkaStorageMode.get().uppercase())
            } catch (e: IllegalArgumentException) {
                CrafkaStorageMode.DISABLED
            }
        }

    val crafkaCursorRevalidationDelay: Long
        get() = ConfigHolder.serverConfig.crafkaCursorRevalidationDelay.get()

    val crafkaTopicSizeLimit: Int
        get() = ConfigHolder.serverConfig.crafkaTopicSizeLimit.get()
    val crafkaMessageSizeLimit: Int
        get() = ConfigHolder.serverConfig.crafkaMessageSizeLimit.get()
    val crafkaMaxResendSteps: Int
        get() = ConfigHolder.serverConfig.crafkaMaxResendSteps.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // Generic plugins
        val enableStatsDBridge: ForgeConfigSpec.BooleanValue
        val enableKVBridge: ForgeConfigSpec.BooleanValue
        val enableCrafkaBroker: ForgeConfigSpec.BooleanValue

        init {
            builder.push("statsd")
            enableStatsDBridge = builder.comment("Enables statsd bridge")
                .define("enableStatsDBridge", true)
            enableKVBridge = builder.comment("Enables KV storage")
                .define("enableKVStorage", true)
            enableCrafkaBroker = builder.comment("Enable crafka broker")
                .define("enableCrafkaBroker", true)
            builder.pop()
        }
    }

    class ServerConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // StatsD
        val enableStatsDConnection: ForgeConfigSpec.BooleanValue
        val statsDPort: ForgeConfigSpec.IntValue
        val statsDHostName: ForgeConfigSpec.ConfigValue<String>
        val statsDPrefix: ForgeConfigSpec.ConfigValue<String>
        val statsDPlayerRateLimit: ForgeConfigSpec.IntValue
        val statsDGlobalRateLimit: ForgeConfigSpec.IntValue

        // Data storage
        val kvStorageMode: ForgeConfigSpec.ConfigValue<String>
        val kvStorageKeyLimit: ForgeConfigSpec.IntValue
        val kvStorageValueLimit: ForgeConfigSpec.IntValue

        // Crafka broker
        val crafkaStorageMode: ForgeConfigSpec.ConfigValue<String>
        val crafkaCursorRevalidationDelay: ForgeConfigSpec.LongValue
        val crafkaTopicSizeLimit: ForgeConfigSpec.IntValue
        val crafkaMessageSizeLimit: ForgeConfigSpec.IntValue
        val crafkaMaxResendSteps: ForgeConfigSpec.IntValue

        init {
            builder.push("statsd")
            enableStatsDConnection = builder.comment("StatsD connection")
                .define("enableStatsDConnection", false)
            statsDPort = builder.comment("StatsD port")
                .defineInRange("statsdPort", 8125, 1, 65555)
            statsDHostName = builder.comment("StatsD hostname")
                .define("statsdHostname", "127.0.0.1")
            statsDPrefix = builder.comment("StatsD prefix")
                .define("statsdPrefix", "")
            statsDPlayerRateLimit = builder.comment("StatsD rate limit per player in event per minute")
                .defineInRange("statsdPlayerRateLimit", 1000, 1, Int.MAX_VALUE)
            statsDGlobalRateLimit = builder.comment("StatsD global rate limit in event per minute")
                .defineInRange("statsdGlobalRateLimit", 21_000, 1, Int.MAX_VALUE)
            builder.pop()
            builder.push("kv")
            kvStorageMode = builder.comment("Mode of KV storage")
                .define("kvStorageMode", KVStorageMode.SQLITE.name) {
                    if (it == null) return@define false
                    return@define try {
                        KVStorageMode.valueOf(it.toString().uppercase())
                        true
                    } catch (e: IllegalArgumentException) {
                        false
                    }
                }
            kvStorageKeyLimit = builder.comment("Limit for active keys in storage per player")
                .defineInRange("kvStorageKeyLimit", 10240, 1, Int.MAX_VALUE)
            kvStorageValueLimit = builder.comment("Limit for max size of value")
                .defineInRange("kvStorageValueLimit", 500_000, 1, Int.MAX_VALUE)
            builder.pop()
            builder.push("crafka")
            crafkaStorageMode = builder.comment("Mode of Crafka broker storage")
                .define("crafkaStorageMod", CrafkaStorageMode.SQLITE.name) {
                    if (it == null) return@define false
                    return@define try {
                        CrafkaStorageMode.valueOf(it.toString().uppercase())
                        true
                    } catch (e: IllegalArgumentException) {
                        false
                    }
                }
            crafkaCursorRevalidationDelay = builder.comment("Time in millisecond how fast crafka schedule check for message delivery")
                .defineInRange("crafkaCursorRevalidationDelay", 100L, 0L, Long.MAX_VALUE)
            crafkaTopicSizeLimit = builder.comment("Limit for amount of topics per player")
                .defineInRange("crafkaTopicSizeLimit", 512, 0, Int.MAX_VALUE)
            crafkaMessageSizeLimit = builder.comment("Limit for amount of topics per player")
                .defineInRange("crafkaMessageSizeLimit", 500_000, 0, Int.MAX_VALUE)
            crafkaMaxResendSteps = builder.comment("Max amount of tries to resend message before subscription will drop")
                .defineInRange("crafkaMaxResendSteps", 21, 5, 128)
            builder.pop()
        }
    }
}
