package site.siredvin.datafortress.subsystems.kv.sqlite

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object KVRecords : Table() {
    val c_ownerUUID = varchar("owner_uuid", 255)
    val c_key = varchar("key", 255)
    val c_value = text("value")
    val c_expire = timestamp("expire").nullable()

    init {
        index(true, c_ownerUUID, c_key)
        index(false, c_expire)
    }
}
