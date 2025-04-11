package de.libf.taigamp.domain.entities

import de.libf.taigamp.domain.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikiPage(
    val id: Long,
    val version: Int,
    val content: String,
    val editions: Long,
    @SerialName("created_date") @Serializable(with = LocalDateTimeSerializer::class) val cratedDate: LocalDateTime,
    @SerialName("is_watcher") val isWatcher: Boolean,
    @SerialName("last_modifier") val lastModifier: Long,
    @SerialName("modified_date") @Serializable(with = LocalDateTimeSerializer::class) val modifiedDate: LocalDateTime,
    @SerialName("total_watchers") val totalWatchers: Long,
    @SerialName("slug")val slug: String
)

@Serializable
data class WikiLink(
    @SerialName("href") val ref: String,
    val id: Long,
    val order: Long,
    val title: String
)