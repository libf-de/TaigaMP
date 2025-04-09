package de.libf.taigamp.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Users related entities
 */

@Serializable
data class User(
    @SerialName("id") val _id: Long?,
    @SerialName("full_name_display") val fullName: String?,
    val photo: String?,
    @SerialName("big_photo") val bigPhoto: String?,
    val username: String,
    val name: String? = null, // sometimes name appears here
    val pk: Long? = null
) {
    val displayName get() = fullName ?: name!!
    val avatarUrl get() = bigPhoto ?: photo
    val id get() = _id ?: pk!!
}



data class TeamMember(
    val id: Long,
    val avatarUrl: String?,
    val name: String,
    val role: String,
    val username: String,
    val totalPower: Int
) {
    fun toUser() = User(
        _id = id,
        fullName = name,
        photo = avatarUrl,
        bigPhoto = null,
        username = username
    )
}

@Serializable
data class Stats(
    val roles: List<String> = emptyList(),
    @SerialName("total_num_closed_userstories")
    val totalNumClosedUserStories: Int,
    @SerialName("total_num_contacts")
    val totalNumContacts: Int,
    @SerialName("total_num_projects")
    val totalNumProjects: Int,
)