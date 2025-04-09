package de.libf.taigamp.data.api

import de.libf.taigamp.domain.entities.Attachment
import de.libf.taigamp.domain.entities.Comment
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.domain.entities.Stats
import de.libf.taigamp.domain.entities.Swimlane
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.domain.entities.WikiLink
import de.libf.taigamp.domain.entities.WikiPage
import io.github.aakira.napier.Napier
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.cio.Response

class TestApi : TaigaApi {
    override suspend fun auth(authRequest: AuthRequest): AuthResponse {
        Napier.d("AUTH: ${authRequest.type}, ${authRequest.username}, ${authRequest.password}")

        return AuthResponse(
            "token",
            "refresh",
            1
        )
    }

    override suspend fun getProjects(
        query: String?,
        page: Int?,
        memberId: Long?,
        pageSize: Int?
    ): List<Project> {
        Napier.d("GET PROJECTS: $query, $page, $memberId, $pageSize")
        return emptyList()
    }

    override suspend fun getProject(projectId: Long): ProjectResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userId: Long): User {
        TODO("Not yet implemented")
    }

    override suspend fun getMyProfile(): User {
        TODO("Not yet implemented")
    }

    override suspend fun getUserStats(userId: Long): Stats {
        TODO("Not yet implemented")
    }

    override suspend fun getMemberStats(projectId: Long): MemberStatsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getSprints(
        project: Long,
        page: Int,
        isClosed: Boolean
    ): List<SprintResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSprint(sprintId: Long): SprintResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createSprint(request: CreateSprintRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun editSprint(id: Long, request: EditSprintRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSprint(id: Long): Response {
        TODO("Not yet implemented")
    }

    override suspend fun getCommonTaskFiltersData(
        taskPath: CommonTaskPathPlural,
        project: Long,
        milestone: String?
    ): FiltersDataResponse {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun getUserStoryByRef(projectId: Long, ref: Int): CommonTaskResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCommonTask(
        taskPath: CommonTaskPathPlural,
        id: Long
    ): CommonTaskResponse {
        TODO("Not yet implemented")
    }

    override suspend fun editCommonTask(
        taskPath: CommonTaskPathPlural,
        id: Long,
        editCommonTaskRequest: EditCommonTaskRequest
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun createCommonTask(
        taskPath: CommonTaskPathPlural,
        createRequest: CreateCommonTaskRequest
    ): CommonTaskResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createTask(createTaskRequest: CreateTaskRequest): CommonTaskResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createIssue(createIssueRequest: CreateIssueRequest): CommonTaskResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createUserstory(createUserStoryRequest: CreateUserStoryRequest): CommonTaskResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCommonTask(taskPath: CommonTaskPathPlural, id: Long): Response {
        TODO("Not yet implemented")
    }

    override suspend fun linkToEpic(epicId: Long, linkToEpicRequest: LinkToEpicRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun unlinkFromEpic(epicId: Long, userStoryId: Long): Response {
        TODO("Not yet implemented")
    }

    override suspend fun promoteCommonTaskToUserStory(
        taskPath: CommonTaskPathPlural,
        taskId: Long,
        promoteToUserStoryRequest: PromoteToUserStoryRequest
    ): List<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun createCommonTaskComment(
        taskPath: CommonTaskPathPlural,
        id: Long,
        createCommentRequest: CreateCommentRequest
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getCommonTaskComments(
        taskPath: CommonTaskPathSingular,
        id: Long
    ): List<Comment> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCommonTaskComment(
        taskPath: CommonTaskPathSingular,
        id: Long,
        commentId: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getCommonTaskAttachments(
        taskPath: CommonTaskPathPlural,
        storyId: Long,
        projectId: Long
    ): List<Attachment> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCommonTaskAttachment(
        taskPath: CommonTaskPathPlural,
        attachmentId: Long
    ): Response {
        TODO("Not yet implemented")
    }

    override suspend fun uploadCommonTaskAttachment(
        taskPath: CommonTaskPathPlural,
        multipartBody: MultiPartFormDataContent
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomAttributes(
        taskPath: CommonTaskPathSingular,
        projectId: Long
    ): List<CustomAttributeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomAttributesValues(
        taskPath: CommonTaskPathPlural,
        taskId: Long
    ): CustomAttributesValuesResponse {
        TODO("Not yet implemented")
    }

    override suspend fun editCustomAttributesValues(
        taskPath: CommonTaskPathPlural,
        taskId: Long,
        editRequest: EditCustomAttributesValuesRequest
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getSwimlanes(project: Long): List<Swimlane> {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectWikiPages(projectId: Long): List<WikiPage> {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectWikiPageBySlug(projectId: Long, slug: String): WikiPage {
        TODO("Not yet implemented")
    }

    override suspend fun editWikiPage(pageId: Long, editWikiPageRequest: EditWikiPageRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun getPageAttachments(pageId: Long, projectId: Long): List<Attachment> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPageAttachment(multipartBody: MultiPartFormDataContent) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePageAttachment(attachmentId: Long): Response {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWikiPage(pageId: Long): Response {
        TODO("Not yet implemented")
    }

    override suspend fun getWikiLink(projectId: Long): List<WikiLink> {
        TODO("Not yet implemented")
    }

    override suspend fun createWikiLink(newWikiLinkRequest: NewWikiLinkRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWikiLink(linkId: Long): Response {
        TODO("Not yet implemented")
    }
}