package de.libf.taigamp.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Swimlane(
    val id: Long,
    val name: String,
    val order: Long
)
