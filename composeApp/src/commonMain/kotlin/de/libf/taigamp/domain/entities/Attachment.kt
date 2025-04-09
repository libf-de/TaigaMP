package de.libf.taigamp.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Long,
    val name: String,
    @SerialName("size") val sizeInBytes: Long,
    val url: String
)
