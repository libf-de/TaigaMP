package de.libf.taigamp.domain.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikiPage(
    val id: Long,
    val version: Int,
    val content: String,
    val editions: Long,
    @SerialName("created_date") val cratedDate: LocalDateTime,
    @SerialName("is_watcher") val isWatcher: Boolean,
    @SerialName("last_modifier") val lastModifier: Long,
    @SerialName("modified_date") val modifiedDate: LocalDateTime,
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