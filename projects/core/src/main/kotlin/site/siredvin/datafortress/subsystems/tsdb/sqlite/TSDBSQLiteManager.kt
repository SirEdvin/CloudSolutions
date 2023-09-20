package site.siredvin.datafortress.subsystems.tsdb.sqlite

import dan200.computercraft.api.lua.LuaException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import site.siredvin.datafortress.subsystems.tsdb.TSDBManager
import site.siredvin.datafortress.subsystems.tsdb.TSDBManagerException
import site.siredvin.datafortress.subsystems.tsdb.TimeseriesFrame
import site.siredvin.datafortress.subsystems.tsdb.TimeseriesInformation
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object TSDBSQLiteManager : TSDBManager {

    val dateFormatter = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss.SSS",
        Locale.ROOT,
    ).withZone(ZoneId.systemDefault())

    override fun init() {
        Database.connect("jdbc:sqlite:./test.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Measurements, Timeserieses)
        }
    }

    @Throws(TSDBManagerException::class)
    override fun query(
        ownerUUID: String,
        namePattern: String,
        tags: Map<String, String>,
        from: Instant,
        to: Instant,
    ): List<TimeseriesFrame> {
        if (from >= to) {
            throw TSDBManagerException("From timestamp should be lower then to timestamp")
        }
        val sqlNamePattern = if (namePattern.endsWith("*")) {
            namePattern.removeSuffix("*") + "%"
        } else {
            namePattern
        }
        // TODO: consider tag pattern usage (?)
        return transaction {
            val timeserieses = Timeserieses.select {
                Timeserieses.c_ownerUUID eq ownerUUID
                if (sqlNamePattern.endsWith("%")) {
                    Timeserieses.c_name like sqlNamePattern
                } else {
                    Timeserieses.c_name eq sqlNamePattern
                }
            }.filter {
                val rowTags = it[Timeserieses.c_tags]
                return@filter rowTags.entries.all { entry ->
                    rowTags.getOrDefault(entry.key, entry.value + "__") == entry.value
                }
            }.associateBy { it[Timeserieses.id] }
            val measurements = Measurements.slice(
                Measurements.c_timeseries,
                Measurements.json_timestamps,
                Measurements.json_values,
            ).select {
                Measurements.c_timeseries inList timeserieses.keys
                Measurements.c_timestamp greaterEq from
                Measurements.c_timestamp lessEq to
            }.orderBy(Measurements.c_timestamp).groupBy(Measurements.c_timeseries).toList()
            return@transaction measurements.map { it ->
                val timeseries = timeserieses[it[Measurements.c_timeseries]]!!
                TimeseriesFrame(
                    timeseries[Timeserieses.c_name],
                    timeseries[Timeserieses.c_tags],
                    it[Measurements.json_timestamps].map { Instant.from(dateFormatter.parse(it)) },
                    it[Measurements.json_values],
                )
            }
        }
    }

    override fun getOrCreate(ownerUUID: String, name: String, tags: Map<String, String>, retention: Int): UUID {
        return transaction {
            val expectedTimeseries = Timeserieses.select {
                (Timeserieses.c_ownerUUID eq ownerUUID) and
                    (Timeserieses.c_name eq name) and
                    (Timeserieses.c_tags eq tags)
            }.singleOrNull()
            if (expectedTimeseries != null) return@transaction expectedTimeseries[Timeserieses.id]
            return@transaction Timeserieses.insert {
                it[c_name] = name
                it[c_retenion] = retention
                it[c_ownerUUID] = ownerUUID
                it[c_tags] = tags
            } get (Timeserieses.id)
        }.value
    }

    override fun update(ownerUUID: String, timeseriesId: UUID, retention: Int) {
        transaction {
            Timeserieses.update({
                Timeserieses.id eq timeseriesId
                Timeserieses.c_ownerUUID eq ownerUUID
            }) {
                it[c_retenion] = retention
            }
        }
    }

    override fun delete(ownerUUID: String, timeseriesId: UUID) {
        transaction {
            Timeserieses.deleteWhere {
                this.id eq timeseriesId
                this.c_ownerUUID eq ownerUUID
            }
        }
    }

    override fun list(ownerUUID: String): List<TimeseriesInformation> {
        return transaction {
            return@transaction Timeserieses.select {
                Timeserieses.c_ownerUUID eq ownerUUID
            }.map {
                TimeseriesInformation(
                    it[Timeserieses.c_name],
                    it[Timeserieses.c_tags],
                    it[Timeserieses.c_retenion],
                )
            }
        }
    }

    override fun put(ownerUUID: String, timeseriesId: UUID, measurementValue: Double) {
        transaction {
            val timeseriesRow = Timeserieses.select {
                (Timeserieses.id eq timeseriesId) and (Timeserieses.c_ownerUUID eq ownerUUID)
            }.singleOrNull() ?: throw LuaException("Cannot find timeseries with id $timeseriesId")
            Measurements.insert {
                it[c_timeseries] = timeseriesRow[Timeserieses.id]
                it[c_v] = measurementValue
            }
        }
    }

    override fun putMany(ownerUUID: String, values: Map<UUID, Double>) {
        transaction {
            val timeseriesRows = Timeserieses.select {
                (Timeserieses.id inList values.keys) and (Timeserieses.c_ownerUUID eq ownerUUID)
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

    fun backfill(ownerUUID: String, timeseriesId: UUID, value: Double, timestamp: Instant) {
        transaction {
            val timeseriesRow = Timeserieses.select {
                (Timeserieses.id eq timeseriesId) and (Timeserieses.c_ownerUUID eq ownerUUID)
            }.singleOrNull() ?: throw LuaException("Cannot find timeseries with id $timeseriesId")
            Measurements.insert {
                it[c_timeseries] = timeseriesRow[Timeserieses.id]
                it[c_v] = value
                it[c_timestamp] = timestamp
            }
        }
    }

    fun backfillMany(ownerUUID: String, values: Map<UUID, Pair<Double, Instant>>) {
        transaction {
            val timeseriesRows = Timeserieses.select {
                (Timeserieses.id inList values.keys) and (Timeserieses.c_ownerUUID eq ownerUUID)
            }.associate { it[Timeserieses.id].value to it[Timeserieses.id] }
            if (timeseriesRows.size != values.size) {
                // So, this means that some ids are missing!
                val missedIds = values.keys.filter { v -> !timeseriesRows.containsKey(v) }
                throw LuaException("Cannot find timeseries with ids: ${missedIds.joinToString(",")}")
            }
            Measurements.batchInsert(values.entries) {
                this[Measurements.c_timeseries] = timeseriesRows[it.key]!!
                this[Measurements.c_v] = it.value.first
                this[Measurements.c_timestamp] = it.value.second
            }
        }
    }
}
