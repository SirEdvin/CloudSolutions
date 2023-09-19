package site.siredvin.datafortress.subsystems.tsdb.sqlite

import dan200.computercraft.api.lua.LuaException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import site.siredvin.datafortress.subsystems.tsdb.TSDBManager
import site.siredvin.datafortress.subsystems.tsdb.TSDBManager.Companion.buildTimeseriesName
import java.time.Instant
import java.util.*

object TSDBSQLiteManager : TSDBManager {

    override fun init() {
        Database.connect("jdbc:sqlite:./test.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Measurements, Timeserieses)
        }
    }

    override fun queryTimeseries(
        ownerUUID: String,
        namePattern: String,
        tags: Map<String, String>,
        from: Instant,
        to: Instant,
    ): Map<String, Map<Long, Double>> {
        if (from >= to) {
            throw LuaException("From timestamp should be lower then to timestamp")
        }
        // TODO: add name pattern transformation
        return transaction {
            val timeserieses = Timeserieses.select {
                Timeserieses.c_ownerUUID eq ownerUUID
                Timeserieses.c_name like namePattern
            }.filter {
                val rowTags = it[Timeserieses.c_tags]
                return@filter rowTags.entries.all { entry ->
                    rowTags.getOrDefault(entry.key, entry.value + "__") == entry.value
                }
            }.associateBy { it[Timeserieses.id] }
            val measurements = Measurements.select {
                Measurements.c_timeseries inList timeserieses.keys
                Measurements.c_timestamp greaterEq from
                Measurements.c_timestamp lessEq to
            }.toList().groupBy { it[Measurements.c_timeseries] }
            return@transaction measurements.entries.associate { entry ->
                val timeseries = timeserieses[entry.key]!!
                buildTimeseriesName(timeseries[Timeserieses.c_name], timeseries[Timeserieses.c_tags]) to entry.value.associate {
                    it[Measurements.c_timestamp].epochSecond to it[Measurements.c_v]
                }
            }
        }
    }

    override fun registerTimeseriers(ownerUUID: String, name: String, tags: Map<String, String>, retention: Int): UUID {
        return transaction {
            return@transaction Timeserieses.insert {
                it[c_name] = name
                it[c_retenion] = retention
                it[c_ownerUUID] = ownerUUID
                it[c_tags] = tags
            } get (Timeserieses.id)
        }.value
    }

    override fun getTimeseries(ownerUUID: String): List<Map<String, Any>> {
        return transaction {
            return@transaction Timeserieses.select {
                Timeserieses.c_ownerUUID eq ownerUUID
            }.map {
                mapOf(
                    "name" to it[Timeserieses.c_name],
                    "retention" to it[Timeserieses.c_retenion],
                    "tags" to it[Timeserieses.c_tags],
                )
            }
        }
    }

    override fun addMeasurement(ownerUUID: String, timeseriesId: UUID, measurementValue: Double) {
        transaction {
            val timeseriesRow = Timeserieses.select {
                Timeserieses.id eq timeseriesId
                Timeserieses.c_ownerUUID eq ownerUUID
            }.singleOrNull() ?: throw LuaException("Cannot find timeseries with id $timeseriesId")
            Measurements.insert {
                it[c_timeseries] = timeseriesRow[Timeserieses.id]
                it[c_v] = measurementValue
            }
        }
    }

    override fun addMeasurements(ownerUUID: String, values: Map<UUID, Double>) {
        transaction {
            val timeseriesRows = Timeserieses.select {
                Timeserieses.id inList values.keys
                Timeserieses.c_ownerUUID eq ownerUUID
            }.associate { it[Timeserieses.id].value to it[Timeserieses.id] }
            if (timeseriesRows.size != values.size) {
                // So, this means that some ids are missing!
                val missedIds = values.keys.filter { v -> !timeseriesRows.containsKey(v) }
                throw LuaException("Cannot find timeseries with ids: ${missedIds.joinToString(",")}")
            }
            Measurements.batchInsert(values.entries) {
                this[Measurements.c_timeseries] = timeseriesRows[it.key]!!
                this[Measurements.c_v] = it.value
            }
        }
    }
}
