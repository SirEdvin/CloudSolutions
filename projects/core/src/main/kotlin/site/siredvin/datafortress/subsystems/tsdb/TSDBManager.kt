package site.siredvin.datafortress.subsystems.tsdb

import java.time.Instant
import java.util.UUID

data class TimeseriesInformation(val name: String, val tags: Map<String, String>, val retention: Int) {
    val fullName: String
        get() {
            val tagsLine = tags.entries.sortedBy { it.key }.joinToString { "${it.key}=${it.value}" }
            return "$name{$tagsLine}"
        }
}

data class TimeseriesFrame(val name: String, val tags: Map<String, String>, val timestamps: List<Instant>, val values: List<Double>) {
    val fullName: String
        get() {
            val tagsLine = tags.entries.sortedBy { it.key }.joinToString { "${it.key}=${it.value}" }
            return "$name{$tagsLine}"
        }

    fun toJson(): Map<String, Any> {
        return mapOf(
            "fullName" to fullName,
            "name" to name,
            "tags" to tags,
            "timestamps" to timestamps.map { it.toString() },
            "values" to values,
        )
    }
}

class TSDBManagerException(message: String) : Exception(message)

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

    @Throws(TSDBManagerException::class)
    fun query(ownerUUID: String, namePattern: String, tags: Map<String, String>, from: Instant, to: Instant): List<TimeseriesFrame>

    @Throws(TSDBManagerException::class)
    fun getOrCreate(ownerUUID: String, name: String, tags: Map<String, String>, retention: Int = DEFAULT_RETENTION): UUID

    @Throws(TSDBManagerException::class)
    fun update(ownerUUID: String, timeseriesId: UUID, retention: Int = DEFAULT_RETENTION)

    @Throws(TSDBManagerException::class)
    fun delete(ownerUUID: String, timeseriesId: UUID)

    @Throws(TSDBManagerException::class)
    fun list(ownerUUID: String): List<TimeseriesInformation>

    @Throws(TSDBManagerException::class)
    fun put(ownerUUID: String, timeseriesId: UUID, measurementValue: Double)

    @Throws(TSDBManagerException::class)
    fun putMany(ownerUUID: String, values: Map<UUID, Double>)
}
