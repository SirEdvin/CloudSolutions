package site.siredvin.datafortress.subsystems.statsq

import com.timgroup.statsd.NonBlockingStatsDClient
import com.timgroup.statsd.StatsDClientException
import site.siredvin.datafortress.DataFortressCore
import site.siredvin.datafortress.common.configuration.ModConfig

object StatsDClient {
    private var client: NonBlockingStatsDClient? = null
    fun init() {
        if (ModConfig.enableStatsDConnection) {
            try {
                this.client = NonBlockingStatsDClient(
                    ModConfig.statsdPrefix,
                    ModConfig.statsdHostname,
                    ModConfig.statsdPort,
                ) { exception -> DataFortressCore.LOGGER.warn("Exception inside statsd client: $exception") }
                DataFortressCore.LOGGER.info("Statsd server started")
            } catch (exc: StatsDClientException) {
                DataFortressCore.LOGGER.warn("Cannot start statsd server, because of $exc")
            }
        } else {
            DataFortressCore.LOGGER.info("Ignoring statsd, because bridge are not enabled")
        }
    }

    fun stop() {
        client?.stop()
    }

    fun count(aspect: String, delta: Long) {
        client?.count(aspect, delta)
    }

    fun delta(aspect: String, value: Long) {
        client?.recordGaugeDelta(aspect, value)
    }

    fun gauge(aspect: String, value: Long) {
        client?.gauge(aspect, value)
    }

    fun set(aspect: String, eventName: String) {
        client?.set(aspect, eventName)
    }

    fun time(aspect: String, timeInMs: Long) {
        client?.time(aspect, timeInMs)
    }
}
