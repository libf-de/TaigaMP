package de.libf.taigamp.domain.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Comment(
    val id: String,
    @SerialName("user") val author: User,
    @SerialName("comment") val text: String,
    @SerialName("created_at") val postDateTime: LocalDateTime,
    @SerialName("delete_comment_date") val deleteDate: LocalDateTime?
) {
    var canDelete: Boolean = false
}
