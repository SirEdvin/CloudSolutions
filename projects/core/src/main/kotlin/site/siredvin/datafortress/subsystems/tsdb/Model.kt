package site.siredvin.datafortress.subsystems.tsdb

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.json

object Timeseries : IntIdTable() {

    val name = varchar("name", 255)
    val ownerUUID = varchar("owner_uuid", 255)
    val tags = json<Map<String, String>>("tags", Json)

    // retention in minutes (!), by default 7 days
    val retenion: Column<Int> = integer("retention")

    init {
        index(true, name, ownerUUID, tags)
    }
}

object Measurements : Table() {
    val timeseries = reference("timeseries", Timeseries)
    val timestamp: Column<Long> = long("timestamp")
    val v: Column<Double> = double("value")

    init {
        index(false, timestamp)
        index(true, timeseries, timestamp)
    }
}
