package site.siredvin.cloudsolutions.subsystems.statsq

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.util.concurrent.RateLimiter
import com.timgroup.statsd.NonBlockingStatsDClient
import com.timgroup.statsd.StatsDClientException
import dan200.computercraft.api.lua.LuaException
import site.siredvin.cloudsolutions.CloudSolutionsCore
import site.siredvin.cloudsolutions.common.configuration.ModConfig
import java.util.concurrent.TimeUnit

object StatsDClient {
    private var client: NonBlockingStatsDClient? = null
    private val globalRateLimiter by lazy {
        RateLimiter.create(ModConfig.statsdGlobalRateLimit.toDouble() / 60)
    }
    private val playerRateLimiters = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES)
        .build(CacheLoader.from { _: String -> RateLimiter.create(ModConfig.statsdPlayerRateLimit.toDouble() / 60) })
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

    private fun validate(playerUUID: String) {
        if (!globalRateLimiter.tryAcquire()) throw LuaException("Too many requests to server, please wait a little")
        if (!playerRateLimiters.get(playerUUID).tryAcquire()) throw LuaException("Too many request from you, please wait a little")
        if (!ModConfig.enableStatsDConnection) throw LuaException("Statsd Bridge is disabled in server configuration")
    }

    fun count(playerUUID: String, aspect: String, delta: Long) {
        validate(playerUUID)
        client?.count(aspect, delta)
    }

    fun delta(playerUUID: String, aspect: String, value: Long) {
        validate(playerUUID)
        client?.recordGaugeDelta(aspect, value)
    }

    fun gauge(playerUUID: String, aspect: String, value: Long) {
        validate(playerUUID)
        client?.gauge(aspect, value)
    }

    fun set(playerUUID: String, aspect: String, eventName: String) {
        validate(playerUUID)
        client?.set(aspect, eventName)
    }

    fun time(playerUUID: String, aspect: String, timeInMs: Long) {
        validate(playerUUID)
        client?.time(aspect, timeInMs)
    }
}
