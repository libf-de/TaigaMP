package de.libf.taigamp.data.api

import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.domain.paging.CommonPagingSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Ktor implementation of the TaigaApi interface
 */
class TaigaKtorClient(private val client: HttpClient) : TaigaApi {
    companion object {
        const val API_PREFIX = TaigaApi.API_PREFIX
        const val AUTH_ENDPOINTS = TaigaApi.AUTH_ENDPOINTS
        const val REFRESH_ENDPOINT = TaigaApi.REFRESH_ENDPOINT
    }

    override suspend fun auth(authRequest: AuthRequest): AuthResponse {
        return client.post("auth") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }.body()
    }

    // Projects

    override suspend fun getProjects(
        query: String?,
        page: Int?,
        memberId: Long?,
        pageSize: Int?
    ): List<Project> {
        return client.get("projects") {
            parameter("order_by", "user_order")
            parameter("slight", "true")
            query?.let { parameter("q", it) }
            page?.let { parameter("page", it) }
            memberId?.let { parameter("member", it) }
            pageSize?.let { parameter("page_size", it) }
        }.body()
    }

    override suspend fun getProject(projectId: Long): ProjectResponse {
        return client.get("projects/$projectId").body()
    }

    // Users

    override suspend fun getUser(userId: Long): User {
        return client.get("users/$userId").body()
    }

    override suspend fun getMyProfile(): User {
        return client.get("users/me").body()
    }

    override suspend fun getUserStats(userId: Long): Stats {
        return client.get("users/$userId/stats").body()
    }

    override suspend fun getMemberStats(projectId: Long): MemberStatsResponse {
        return client.get("projects/$projectId/member_stats").body()
    }

    // Sprints

    override suspend fun getSprints(
        project: Long,
        page: Int,
        isClosed: Boolean
    ): List<SprintResponse> {
        return client.get("milestones") {
            parameter("project", project)
            parameter("page", page)
            parameter("closed", isClosed)
        }.body()
    }

    override suspend fun getSprint(sprintId: Long): SprintResponse {
        return client.get("milestones/$sprintId").body()
    }

    override suspend fun createSprint(request: CreateSprintRequest) {
        client.post("milestones") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun editSprint(id: Long, request: EditSprintRequest) {
        client.patch("milestones/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun deleteSprint(id: Long): io.ktor.http.cio.Response {
        return client.delete("milestones/$id").body()
    }

    // Common Tasks

    override suspend fun getCommonTaskFiltersData(
        taskPath: CommonTaskPathPlural,
        project: Long,
        milestone: String?
    ): FiltersDataResponse {
        return client.get("$taskPath/filters_data") {
            parameter("project", project)
            milestone?.let { parameter("milestone", it) }
        }.body()
    }

    override suspend fun getUserStories(
        project: Long?,
        sprint: Long?,
        status: Long?,
        epic: Long?,
        page: Int?,
        assignedId: Long?,
        isClosed: Boolean?,
        watcherId: Long?,
        isDashboard: Boolean?,
        query: String?,
        pageSize: Int,
        assignedIds: String?,
        epics: String?,
        ownerIds: String?,
        roles: String?,
        statuses: String?,
        tags: String?,
        disablePagination: Boolean?
    ): List<CommonTaskResponse> {
        return client.get("userstories") {
            project?.let { parameter("project", it) }
            sprint?.let { parameter("milestone", it) }
            status?.let { parameter("status", it) }
            epic?.let { parameter("epic", it) }
            page?.let { parameter("page", it) }
            assignedId?.let { parameter("assigned_users", it) }
            isClosed?.let { parameter("status__is_closed", it) }
            watcherId?.let { parameter("watchers", it) }
            isDashboard?.let { parameter("dashboard", it) }
            query?.let { parameter("q", it) }
            parameter("page_size", pageSize)

            assignedIds?.let { parameter("assigned_to", it) }
            epics?.let { parameter("epic", it) }
            ownerIds?.let { parameter("owner", it) }
            roles?.let { parameter("role", it) }
            statuses?.let { parameter("status", it) }
            tags?.let { parameter("tags", it) }

            if (disablePagination != null) {
                header("x-disable-pagination", disablePagination.toString())
            }
        }.body()
    }

    override suspend fun getUserStories(
        project: Long?,
        sprint: String,
        status: Long?,
        epic: Long?,
        page: Int?,
        assignedId: Long?,
        isClosed: Boolean?,
        watcherId: Long?,
        isDashboard: Boolean?,
        query: String?,
        pageSize: Int,
        assignedIds: String?,
        epics: String?,
        ownerIds: String?,
        roles: String?,
        statuses: String?,
        tags: String?,
        disablePagination: Boolean?
    ): List<CommonTaskResponse> {
        return client.get("userstories") {
            project?.let { parameter("project", it) }
            parameter("milestone", sprint)
            status?.let { parameter("status", it) }
            epic?.let { parameter("epic", it) }
            page?.let { parameter("page", it) }
            assignedId?.let { parameter("assigned_users", it) }
            isClosed?.let { parameter("status__is_closed", it) }
            watcherId?.let { parameter("watchers", it) }
            isDashboard?.let { parameter("dashboard", it) }
            query?.let { parameter("q", it) }
            parameter("page_size", pageSize)

            assignedIds?.let { parameter("assigned_to", it) }
            epics?.let { parameter("epic", it) }
            ownerIds?.let { parameter("owner", it) }
            roles?.let { parameter("role", it) }
            statuses?.let { parameter("status", it) }
            tags?.let { parameter("tags", it) }

            if (disablePagination != null) {
                header("x-disable-pagination", disablePagination.toString())
            }
        }.body()
    }

    override suspend fun getTasks(
        userStory: String?,
        project: Long?,
        sprint: Long?,
        page: Int?,
        assignedId: Long?,
        isClosed: Boolean?,
        watcherId: Long?,
        disablePagination: Boolean?
    ): List<CommonTaskResponse> {
        return client.get("tasks") {
            parameter("order_by", "us_order")
            userStory?.let { parameter("user_story", it) }
            project?.let { parameter("project", it) }
            sprint?.let { parameter("milestone", it) }
            page?.let { parameter("page", it) }
            assignedId?.let { parameter("assigned_to", it) }
            isClosed?.let { parameter("status__is_closed", it) }
            watcherId?.let { parameter("watchers", it) }

            if (disablePagination != null) {
                header("x-disable-pagination", disablePagination.toString())
            }
        }.body()
    }

    override suspend fun getTasks(
        userStory: Long,
        project: Long?,
        sprint: Long?,
        page: Int?,
        assignedId: Long?,
        isClosed: Boolean?,
        watcherId: Long?,
        disablePagination: Boolean?
    ): List<CommonTaskResponse> {
        return client.get("tasks") {
            parameter("order_by", "us_order")
            parameter("user_story", userStory)
            project?.let { parameter("project", it) }
            sprint?.let { parameter("milestone", it) }
            page?.let { parameter("page", it) }
            assignedId?.let { parameter("assigned_to", it) }
            isClosed?.let { parameter("status__is_closed", it) }
            watcherId?.let { parameter("watchers", it) }

            if (disablePagination != null) {
                header("x-disable-pagination", disablePagination.toString())
            }
        }.body()
    }

    override suspend fun getEpics(
        page: Int?,
        project: Long?,
        query: String?,
        assignedId: Long?,
        isClosed: Boolean?,
        watcherId: Long?,
        pageSize: Int,
        assignedIds: String?,
        ownerIds: String?,
        statuses: String?,
        tags: String?,
        disablePagination: Boolean?
    ): List<CommonTaskResponse> {
        return client.get("epics") {
            page?.let { parameter("page", it) }
            project?.let { parameter("project", it) }
            query?.let { parameter("q", it) }
            assignedId?.let { parameter("assigned_to", it) }
            isClosed?.let { parameter("status__is_closed", it) }
            watcherId?.let { parameter("watchers", it) }
            parameter("page_size", pageSize)

            assignedIds?.let { parameter("assigned_to", it) }
            ownerIds?.let { parameter("owner", it) }
            statuses?.let { parameter("status", it) }
            tags?.let { parameter("tags", it) }

            if (disablePagination != null) {
                header("x-disable-pagination", disablePagination.toString())
            }
        }.body()
    }

    override suspend fun getIssues(
        page: Int?,
        project: Long?,
        query: String?,
        sprint: Long?,
        isClosed: Boolean?,
        watcherId: Long?,
        pageSize: Int,
        assignedIds: String?,
        ownerIds: String?,
        priorities: String?,
        severities: String?,
        types: String?,
        roles: String?,
        statuses: String?,
        tags: String?,
        disablePagination: Boolean?
    ): List<CommonTaskResponse> {
        return client.get("issues") {
            page?.let { parameter("page", it) }
            project?.let { parameter("project", it) }
            query?.let { parameter("q", it) }
            sprint?.let { parameter("milestone", it) }
            isClosed?.let { parameter("status__is_closed", it) }
            watcherId?.let { parameter("watchers", it) }
            parameter("page_size", pageSize)

            assignedIds?.let { parameter("assigned_to", it) }
            ownerIds?.let { parameter("owner", it) }
            priorities?.let { parameter("priority", it) }
            severities?.let { parameter("severity", it) }
            types?.let { parameter("type", it) }
            roles?.let { parameter("role", it) }
            statuses?.let { parameter("status", it) }
            tags?.let { parameter("tags", it) }

            if (disablePagination != null) {
                header("x-disable-pagination", disablePagination.toString())
            }
        }.body()
    }

    override suspend fun getUserStoryByRef(
        projectId: Long,
        ref: Int
    ): CommonTaskResponse {
        return client.get("userstories/by_ref") {
            parameter("project", projectId)
            parameter("ref", ref)
        }.body()
    }

    override suspend fun getCommonTask(
        taskPath: CommonTaskPathPlural,
        id: Long
    ): CommonTaskResponse {
        return client.get("$taskPath/$id").body()
    }

    override suspend fun editCommonTask(
        taskPath: CommonTaskPathPlural,
        id: Long,
        editCommonTaskRequest: EditCommonTaskRequest
    ) {
        client.patch("$taskPath/$id") {
            contentType(ContentType.Application.Json)
            setBody(editCommonTaskRequest)
        }
    }

    override suspend fun createCommonTask(
        taskPath: CommonTaskPathPlural,
        createRequest: CreateCommonTaskRequest
    ): CommonTaskResponse {
        return client.post(taskPath.path) {
            contentType(ContentType.Application.Json)
            setBody(createRequest)
        }.body()
    }

    override suspend fun createTask(createTaskRequest: CreateTaskRequest): CommonTaskResponse {
        return client.post("tasks") {
            contentType(ContentType.Application.Json)
            setBody(createTaskRequest)
        }.body()
    }

    override suspend fun createIssue(createIssueRequest: CreateIssueRequest): CommonTaskResponse {
        return client.post("issues") {
            contentType(ContentType.Application.Json)
            setBody(createIssueRequest)
        }.body()
    }

    override suspend fun createUserstory(createUserStoryRequest: CreateUserStoryRequest): CommonTaskResponse {
        return client.post("userstories") {
            contentType(ContentType.Application.Json)
            setBody(createUserStoryRequest)
        }.body()
    }

    override suspend fun deleteCommonTask(
        taskPath: CommonTaskPathPlural,
        id: Long
    ): io.ktor.http.cio.Response {
        return client.delete("$taskPath/$id").body()
    }

    override suspend fun linkToEpic(
        epicId: Long,
        linkToEpicRequest: LinkToEpicRequest
    ) {
        client.post("epics/$epicId/related_userstories") {
            contentType(ContentType.Application.Json)
            setBody(linkToEpicRequest)
        }
    }

    override suspend fun unlinkFromEpic(
        epicId: Long,
        userStoryId: Long
    ): io.ktor.http.cio.Response {
        return client.delete("epics/$epicId/related_userstories/$userStoryId").body()
    }

    override suspend fun promoteCommonTaskToUserStory(
        taskPath: CommonTaskPathPlural,
        taskId: Long,
        promoteToUserStoryRequest: PromoteToUserStoryRequest
    ): List<Int> {
        return client.post("$taskPath/$taskId/promote_to_user_story") {
            contentType(ContentType.Application.Json)
            setBody(promoteToUserStoryRequest)
        }.body()
    }

    // Tasks comments

    override suspend fun createCommonTaskComment(
        taskPath: CommonTaskPathPlural,
        id: Long,
        createCommentRequest: CreateCommentRequest
    ) {
        client.patch("$taskPath/$id") {
            contentType(ContentType.Application.Json)
            setBody(createCommentRequest)
        }
    }

    override suspend fun getCommonTaskComments(
        taskPath: CommonTaskPathSingular,
        id: Long
    ): List<Comment> {
        return client.get("history/$taskPath/$id") {
            parameter("type", "comment")
        }.body()
    }

    override suspend fun deleteCommonTaskComment(
        taskPath: CommonTaskPathSingular,
        id: Long,
        commentId: String
    ) {
        client.post("history/$taskPath/$id/delete_comment") {
            parameter("id", commentId)
        }
    }

    // Tasks attachments

    override suspend fun getCommonTaskAttachments(
        taskPath: CommonTaskPathPlural,
        storyId: Long,
        projectId: Long
    ): List<Attachment> {
        return client.get("$taskPath/attachments") {
            parameter("object_id", storyId)
            parameter("project", projectId)
        }.body()
    }

    override suspend fun deleteCommonTaskAttachment(
        taskPath: CommonTaskPathPlural,
        attachmentId: Long
    ): io.ktor.http.cio.Response {
        return client.delete("$taskPath/attachments/$attachmentId").body()
    }

    override suspend fun uploadCommonTaskAttachment(
        taskPath: CommonTaskPathPlural,
        multipartBody: MultiPartFormDataContent
    ) {
        client.post("$taskPath/attachments") {
            setBody(multipartBody)
        }
    }

    // Custom attributes

    override suspend fun getCustomAttributes(
        taskPath: CommonTaskPathSingular,
        projectId: Long
    ): List<CustomAttributeResponse> {
        return client.get("$taskPath-custom-attributes") {
            parameter("project", projectId)
        }.body()
    }

    override suspend fun getCustomAttributesValues(
        taskPath: CommonTaskPathPlural,
        taskId: Long
    ): CustomAttributesValuesResponse {
        return client.get("$taskPath/custom-attributes-values/$taskId").body()
    }

    override suspend fun editCustomAttributesValues(
        taskPath: CommonTaskPathPlural,
        taskId: Long,
        editRequest: EditCustomAttributesValuesRequest
    ) {
        client.patch("$taskPath/custom-attributes-values/$taskId") {
            contentType(ContentType.Application.Json)
            setBody(editRequest)
        }
    }

    // Swimlanes

    override suspend fun getSwimlanes(project: Long): List<Swimlane> {
        return client.get("swimlanes") {
            parameter("project", project)
        }.body()
    }

    // Wiki

    override suspend fun getProjectWikiPages(projectId: Long): List<WikiPage> {
        return client.get("wiki") {
            parameter("project", projectId)
        }.body()
    }

    override suspend fun getProjectWikiPageBySlug(
        projectId: Long,
        slug: String
    ): WikiPage {
        return client.get("wiki/by_slug") {
            parameter("project", projectId)
            parameter("slug", slug)
        }.body()
    }

    override suspend fun editWikiPage(
        pageId: Long,
        editWikiPageRequest: EditWikiPageRequest
    ) {
        client.patch("wiki/$pageId") {
            contentType(ContentType.Application.Json)
            setBody(editWikiPageRequest)
        }
    }

    override suspend fun getPageAttachments(
        pageId: Long,
        projectId: Long
    ): List<Attachment> {
        return client.get("wiki/attachments") {
            parameter("object_id", pageId)
            parameter("project", projectId)
        }.body()
    }

    override suspend fun uploadPageAttachment(multipartBody: MultiPartFormDataContent) {
        client.post("wiki/attachments") {
            setBody(multipartBody)
        }
    }

    override suspend fun deletePageAttachment(attachmentId: Long): io.ktor.http.cio.Response {
        return client.delete("wiki/attachments/$attachmentId").body()
    }

    override suspend fun deleteWikiPage(pageId: Long): io.ktor.http.cio.Response {
        return client.delete("wiki/$pageId").body()
    }

    override suspend fun getWikiLink(projectId: Long): List<WikiLink> {
        return client.get("wiki-links") {
            parameter("project", projectId)
        }.body()
    }

    override suspend fun createWikiLink(newWikiLinkRequest: NewWikiLinkRequest) {
        client.post("wiki-links") {
            contentType(ContentType.Application.Json)
            setBody(newWikiLinkRequest)
        }
    }

    override suspend fun deleteWikiLink(linkId: Long): io.ktor.http.cio.Response {
        return client.delete("wiki-links/$linkId").body()
    }
}
