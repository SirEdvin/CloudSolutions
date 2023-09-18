package site.siredvin.datafortress.subsystems.tsdb

import dan200.computercraft.api.lua.LuaException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object TSDBManager {

    const val DEFAULT_RETENTION = 60 * 24 * 7

    fun init() {
        Database.connect("jdbc:sqlite:./test.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Measurements, Timeseries)
        }
    }

    private fun buildTimeseriesName(timeseriesName: String, timeseriesTags: Map<String, String>): String {
        val tags = timeseriesTags.entries.sortedBy { it.key }.joinToString { "${it.key}=${it.value}" }
        return "$timeseriesName{$tags}"
    }

    fun queryTimeseries(
        timeseriesOwnerUUID: String,
        timeseriesNamePattern: String,
        timeseriesTags: Map<String, String>,
        fromTimestamp: Long,
        toTimestamp: Long,
    ): Map<String, Map<Long, Double>> {
        if (fromTimestamp >= toTimestamp) {
            throw LuaException("From timestamp should be lower then to timestamp")
        }
        // TODO: add name pattern transformation
        return transaction {
            val timeserieses = Timeseries.select {
                Timeseries.ownerUUID eq timeseriesOwnerUUID
                Timeseries.name like timeseriesNamePattern
            }.filter {
                val tags = it[Timeseries.tags]
                return@filter timeseriesTags.entries.all { entry ->
                    tags.getOrDefault(entry.key, entry.value + "HAHAHAHAHAHA") == entry.value
                }
            }.associateBy { it[Timeseries.id] }
            val measurements = Measurements.select {
                Measurements.timeseries inList timeserieses.keys
                Measurements.timestamp greaterEq fromTimestamp
                Measurements.timestamp lessEq toTimestamp
            }.toList().groupBy { it[Measurements.timeseries] }
            return@transaction measurements.entries.associate { entry ->
                val timeseries = timeserieses[entry.key]!!
                buildTimeseriesName(timeseries[Timeseries.name], timeseries[Timeseries.tags]) to entry.value.associate {
                    it[Measurements.timestamp] to it[Measurements.v]
                }
            }
        }
    }

    fun registerTimeseriers(timeseriesOwnerUUID: String, timeseriesName: String, timeseriesTags: Map<String, String>, timeseriesRetention: Int = DEFAULT_RETENTION): EntityID<Int> {
        return transaction {
            return@transaction Timeseries.insert {
                it[name] = timeseriesName
                it[retenion] = timeseriesRetention
                it[ownerUUID] = timeseriesOwnerUUID
                it[tags] = timeseriesTags
            } get (Timeseries.id)
        }
    }

    fun getTimeseries(timeseriesOwnerUUID: String): List<Map<String, Any>> {
        return transaction {
            return@transaction Timeseries.select {
                Timeseries.ownerUUID eq timeseriesOwnerUUID
            }.map {
                mapOf(
                    "name" to it[Timeseries.name],
                    "retention" to it[Timeseries.retenion],
                    "tags" to it[Timeseries.tags],
                )
            }
        }
    }

    fun addMeasurement(timeseriesOwnerUUID: String, timeseriesId: Int, measurementValue: Double) {
        transaction {
            val timeseriesRow = Timeseries.select {
                Timeseries.id eq timeseriesId
                Timeseries.ownerUUID eq timeseriesOwnerUUID
            }.singleOrNull() ?: throw LuaException("Cannot find timeseries with id $timeseriesId")
            Measurements.insert {
                it[timeseries] = timeseriesRow[Timeseries.id]
                it[timestamp] = Instant.now().epochSecond
                it[v] = measurementValue
            }
        }
    }
}
