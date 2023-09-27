package site.siredvin.cloudsolutions.subsystems.statsq

import com.timgroup.statsd.NonBlockingStatsDClient
import com.timgroup.statsd.StatsDClientException
import dan200.computercraft.api.lua.LuaException
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig

object StatsDClient {
    private var client: NonBlockingStatsDClient? = null
    fun init() {
        if (ModConfig.enableStatsDConnection) {
            try {
                this.client = NonBlockingStatsDClient(
                    ModConfig.statsdPrefix,
                    ModConfig.statsdHostname,
                    ModConfig.statsdPort,
                ) { exception -> CloudSolutionsCore.LOGGER.warn("Exception inside statsd client: $exception") }
                CloudSolutionsCore.LOGGER.info("Statsd server started")
            } catch (exc: StatsDClientException) {
                CloudSolutionsCore.LOGGER.warn("Cannot start statsd server, because of $exc")
            }
        } else {
            CloudSolutionsCore.LOGGER.info("Ignoring statsd, because bridge are not enabled")
        }
    }

    fun stop() {
        client?.stop()
    }

    fun count(aspect: String, delta: Long) {
        if (!ModConfig.enableStatsDConnection) throw LuaException("Statsd Bridge is disabled in server configuration")
        client?.count(aspect, delta)
    }

    fun delta(aspect: String, value: Long) {
        if (!ModConfig.enableStatsDConnection) throw LuaException("Statsd Bridge is disabled in server configuration")
        client?.recordGaugeDelta(aspect, value)
    }

    fun gauge(aspect: String, value: Long) {
        if (!ModConfig.enableStatsDConnection) throw LuaException("Statsd Bridge is disabled in server configuration")
        client?.gauge(aspect, value)
    }

    fun set(aspect: String, eventName: String) {
        if (!ModConfig.enableStatsDConnection) throw LuaException("Statsd Bridge is disabled in server configuration")
        client?.set(aspect, eventName)
    }

    fun time(aspect: String, timeInMs: Long) {
        if (!ModConfig.enableStatsDConnection) throw LuaException("Statsd Bridge is disabled in server configuration")
        client?.time(aspect, timeInMs)
    }
}
