package site.siredvin.cloudsolutions.subsystems.crafka

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object CrafkaMessage : Table("crafka_message") {
    val messageID = integer("message_id")
    val topicID = integer("topic_id").references(CrafkaTopic.id, onDelete = ReferenceOption.CASCADE)
    val value = text("value")
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(topicID, messageID)
}
