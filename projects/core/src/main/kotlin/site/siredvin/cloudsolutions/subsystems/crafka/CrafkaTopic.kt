package site.siredvin.cloudsolutions.subsystems.crafka

import org.jetbrains.exposed.v1.core.Table

object CrafkaTopic : Table("crafka_topic") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val ownerUUID = varchar("ownerUUID", 255)
    val messageLimit = integer("message_limit")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)

    init {
        uniqueIndex(ownerUUID, name)
    }
}
