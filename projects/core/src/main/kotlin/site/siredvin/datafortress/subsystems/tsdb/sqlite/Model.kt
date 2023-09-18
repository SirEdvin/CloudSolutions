package site.siredvin.datafortress.subsystems.tsdb.sqlite

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.json
import java.time.Instant

object KVRecords: Table() {
    val c_ownerUUID = varchar("owner_uuid", 255)
    val c_key = varchar("key", 255)
    val c_value = text("value")
    val c_expire = timestamp("expire").nullable()

    init {
        index(true, c_ownerUUID, c_key)
        index(false, c_expire)
    }
}

object Timeserieses : UUIDTable() {

    val c_name = varchar("name", 255)
    val c_ownerUUID = varchar("owner_uuid", 255)
    val c_tags = json<Map<String, String>>("tags", Json)

    // retention in minutes (!), by default 7 days
    val c_retenion = integer("retention")

    init {
        index(true, c_name, c_ownerUUID, c_tags)

    }
}

object Measurements : Table() {
    val c_timeseries = reference("timeseries", Timeserieses)
    val c_timestamp= timestamp("timestamp").clientDefault { Instant.now() }
    val c_v = double("value")

    init {
        index(false, c_timestamp)
        index(true, c_timeseries, c_timestamp)
    }
}
