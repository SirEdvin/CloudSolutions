package site.siredvin.datafortress.subsystems.tsdb.sqlite

import java.time.Instant
import java.util.*

val ZERO_UUID = UUID(0, 0)

fun main() {
    TSDBSQLiteManager.init()
    val values = TSDBSQLiteManager.query(ZERO_UUID.toString(), "t*", emptyMap(), Instant.now().minusSeconds(1000), Instant.now())
    println(values)
}
