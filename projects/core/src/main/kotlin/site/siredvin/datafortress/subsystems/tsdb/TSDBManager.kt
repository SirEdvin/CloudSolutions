package site.siredvin.datafortress.subsystems.tsdb

import java.time.Instant
import java.util.UUID

interface TSDBManager {
    companion object {
        const val DEFAULT_RETENTION = 60 * 24 * 7
        fun buildTagsString(tags: Map<String, String>): String {
            return tags.entries.sortedBy { it.key }.joinToString { "${it.key}=${it.value}" }
        }

        fun buildTimeseriesName(name: String, tags: Map<String, String>): String {
            val tagsLine = buildTagsString(tags)
            return "$name{$tagsLine}"
        }
    }

    fun init()

    fun queryTimeseries(
        ownerUUID: String,
        namePattern: String,
        tags: Map<String, String>,
        from: Instant,
        to: Instant,
    ): Map<String, Map<Long, Double>>

    fun registerTimeseriers(ownerUUID: String, name: String, tags: Map<String, String>, retention: Int = DEFAULT_RETENTION): UUID
    fun getTimeseries(ownerUUID: String): List<Map<String, Any>>
    fun addMeasurement(ownerUUID: String, timeseriesId: UUID, measurementValue: Double)
    fun addMeasurements(ownerUUID: String, values: Map<UUID, Double>)
}