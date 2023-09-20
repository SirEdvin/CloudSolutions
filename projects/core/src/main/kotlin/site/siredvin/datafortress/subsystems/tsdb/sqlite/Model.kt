package site.siredvin.datafortress.subsystems.tsdb.sqlite

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.JsonColumnType
import org.jetbrains.exposed.sql.json.json
import java.time.Instant

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
    val c_timestamp = timestamp("timestamp").clientDefault { Instant.now() }
    val c_v = double("value")

    val json_timestamps by lazy {
        CustomFunction<List<String>>(
            "json_group_array",
            JsonColumnType(
                { Json.encodeToString(serializer<List<String>>(), it) },
                { Json.decodeFromString(serializer<List<String>>(), it) },
            ),
            c_timestamp,
        )
    }

    val json_values by lazy {
        CustomFunction<List<Double>>(
            "json_group_array",
            JsonColumnType(
                { Json.encodeToString(serializer<List<Double>>(), it) },
                { Json.decodeFromString(serializer<List<Double>>(), it) },
            ),
            c_v,
        )
    }

    init {
        index(false, c_timestamp)
        index(true, c_timeseries, c_timestamp)
    }
}
