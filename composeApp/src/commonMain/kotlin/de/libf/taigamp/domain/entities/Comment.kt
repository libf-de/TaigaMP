package de.libf.taigamp.domain.entities

import de.libf.taigamp.domain.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Comment(
    val id: String,
    @SerialName("user") val author: User,
    @SerialName("comment") val text: String,
    @SerialName("created_at") @Serializable(with = LocalDateTimeSerializer::class) val postDateTime: LocalDateTime,
    @SerialName("delete_comment_date") @Serializable(with = LocalDateTimeSerializer::class) val deleteDate: LocalDateTime?
) {
    var canDelete: Boolean = false
}
