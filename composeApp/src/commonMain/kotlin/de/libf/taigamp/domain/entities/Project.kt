package de.libf.taigamp.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project related entities
 */

@Serializable
data class Project(
    val id: Long,
    val name: String,
    val slug: String,
    @SerialName("i_am_member") val isMember: Boolean = false,
    @SerialName("i_am_admin") val isAdmin: Boolean = false,
    @SerialName("i_am_owner") val isOwner: Boolean = false,
    val description: String? = null,
    @SerialName("logo_small_url") val avatarUrl: String? = null,
    val members: List<Long> = emptyList(),
    @SerialName("total_fans") val fansCount: Int = 0,
    @SerialName("total_watchers") val watchersCount: Int = 0,
    @SerialName("is_private") val isPrivate: Boolean = false
)