package de.libf.taigamp.domain.entities

import kotlinx.datetime.LocalDate

data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: LocalDate,
    val end: LocalDate,
    val storiesCount: Int,
    val isClosed: Boolean
)
