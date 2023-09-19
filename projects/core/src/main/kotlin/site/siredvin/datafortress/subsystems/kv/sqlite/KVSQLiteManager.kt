package site.siredvin.datafortress.subsystems.kv.sqlite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import site.siredvin.datafortress.subsystems.kv.KeyValueManager
import java.time.Instant

object KVSQLiteManager : KeyValueManager {

    override fun init() {
        Database.connect("jdbc:sqlite:./test_kv.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(KVRecords)
        }
    }

    override fun put(ownerUUID: String, key: String, value: String, expire: Instant?) {
        transaction {
            KVRecords.insert {
                it[c_key] = key
                it[c_ownerUUID] = ownerUUID
                it[c_value] = value
                it[c_expire] = expire
            }
        }
    }

    override fun get(ownerUUID: String, key: String): String? {
        return transaction {
            val record = KVRecords.select {
                KVRecords.c_key eq key
                KVRecords.c_ownerUUID eq ownerUUID
            }.singleOrNull() ?: return@transaction null
            return@transaction record[KVRecords.c_value]
        }
    }

    override fun getExpire(ownerUUID: String, key: String): Instant? {
        return transaction {
            val record = KVRecords.select {
                KVRecords.c_key eq key
                KVRecords.c_ownerUUID eq ownerUUID
            }.singleOrNull() ?: return@transaction null
            return@transaction record[KVRecords.c_expire]
        }
    }

    override fun putExpire(ownerUUID: String, key: String, expire: Instant?) {
        transaction {
            KVRecords.update({
                KVRecords.c_key eq key
                KVRecords.c_ownerUUID eq ownerUUID
            }) {
                it[c_expire] = expire
            }
        }
    }

    override fun list(ownerUUID: String): List<String> {
        return transaction {
            return@transaction KVRecords.select {
                KVRecords.c_ownerUUID eq ownerUUID
            }.map { it[KVRecords.c_key] }
        }
    }
}
