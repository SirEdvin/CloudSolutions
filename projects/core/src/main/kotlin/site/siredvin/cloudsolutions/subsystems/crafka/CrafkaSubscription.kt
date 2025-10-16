package site.siredvin.cloudsolutions.subsystems.crafka

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object CrafkaSubscription : Table("crafka_subscription") {
    val id = integer("id").autoIncrement()
    val topicID = integer("topic_id").references(CrafkaTopic.id, onDelete = ReferenceOption.CASCADE)
    val computerID = integer("computer_id")
    val cursor = integer("cursor")
    val fragile = bool("fragile")
    val autoCursor = bool("autoCursor")
    override val primaryKey: Table.PrimaryKey
        get() = PrimaryKey(id)

    init {
        uniqueIndex(topicID, computerID)
    }
}
