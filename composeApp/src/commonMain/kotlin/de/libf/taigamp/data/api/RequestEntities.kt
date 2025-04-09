package de.libf.taigamp.data.api

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val password: String,
    val username: String,
    val type: String
)

@Serializable
data class RefreshTokenRequest(
    val refresh: String
)

@Serializable
data class EditCommonTaskRequest(
    val subject: String,
    val description: String,
    val status: Long,
    val type: Long?,
    val severity: Long?,
    val priority: Long?,
    val milestone: Long?,
    val assigned_to: Long?,
    val assigned_users: List<Long>,
    val watchers: List<Long>,
    val swimlane: Long?,
    val due_date: LocalDate?,
    val color: String?,
    val tags: List<List<String>>,
    val blocked_note: String,
    val is_blocked: Boolean,
    val version: Int
)

@Serializable
data class CreateCommentRequest(
    val comment: String,
    val version: Int
)

@Serializable
data class CreateCommonTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val status: Long?
)

@Serializable
data class CreateTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
    val user_story: Long?
)

@Serializable
data class CreateIssueRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
)

@Serializable
data class CreateUserStoryRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val status: Long?,
    val swimlane: Long?
)

@Serializable
data class LinkToEpicRequest(
    val epic: String,
    val user_story: Long
)

@Serializable
data class PromoteToUserStoryRequest(
    val project_id: Long
)

//TODO: Check whether @Contextual works to determine serializer for Any at runtime, also see CustomFieldValue
@Serializable
data class EditCustomAttributesValuesRequest(
    val attributes_values: Map<Long, @Contextual Any?>,
    val version: Int
)

@Serializable
data class CreateSprintRequest(
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
    val project: Long
)

@Serializable
data class EditSprintRequest(
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
)

@Serializable
data class EditWikiPageRequest(
    val content: String,
    val version: Int
)

@Serializable
data class NewWikiLinkRequest(
    val href: String,
    val project: Long,
    val title: String
)
