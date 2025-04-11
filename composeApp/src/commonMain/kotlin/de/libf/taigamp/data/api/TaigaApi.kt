package de.libf.taigamp.data.api

import de.jensklingenberg.ktorfit.http.*
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.domain.paging.CommonPagingSource
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.cio.Response

/**
 * All API endpoints
 */
interface TaigaApi {
    companion object {
        const val API_PREFIX = "api/v1"
        const val AUTH_ENDPOINTS = "auth"
        const val REFRESH_ENDPOINT = "auth/refresh"
        const val baseUrlPlaceholder = "https://nothing.nothing"
    }

    @Headers("Content-Type: application/json")
    @POST("auth")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse


    /**
     * Projects
     */

    @GET("projects?order_by=user_order&slight=true")
    suspend fun getProjects(
        @Query("q") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("member") memberId: Long? = null,
        @Query("page_size") pageSize: Int? = null
    ): List<Project>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") projectId: Long): ProjectResponse


    /**
     * Users
     */

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Long): User

    @GET("users/me")
    suspend fun getMyProfile(): User

    @GET("users/{id}/stats")
    suspend fun getUserStats(@Path("id") userId: Long): Stats

    @GET("projects/{id}/member_stats")
    suspend fun getMemberStats(@Path("id") projectId: Long): MemberStatsResponse


    /**
     * Sprints
     */
    @GET("milestones")
    suspend fun getSprints(
        @Query("project") project: Long,
        @Query("page") page: Int,
        @Query("closed") isClosed: Boolean
    ): List<SprintResponse>

    @GET("milestones/{id}")
    suspend fun getSprint(@Path("id") sprintId: Long): SprintResponse

    @Headers("Content-Type: application/json")
    @POST("milestones")
    suspend fun createSprint(@Body request: CreateSprintRequest)

    @Headers("Content-Type: application/json")
    @PATCH("milestones/{id}")
    suspend fun editSprint(
        @Path("id") id: Long,
        @Body request: EditSprintRequest
    )

    @DELETE("milestones/{id}")
    suspend fun deleteSprint(@Path("id") id: Long): Response

    /**
     * Everything related to common tasks (epics, user stories, etc.)
     */

    @GET("{taskPath}/filters_data")
    suspend fun getCommonTaskFiltersData(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Query("project") project: Long,
        //@Query("milestone") milestone: Any? = null
        @Query("milestone") milestone: String? = null //ANY
    ): FiltersDataResponse

    @GET("userstories")
    suspend fun getUserStories(
        @Query("project") project: Long? = null,
        @Query("milestone") sprint: Long? = null, //ANY
        @Query("status") status: Long? = null,
        @Query("epic") epic: Long? = null,
        @Query("page") page: Int? = null,
        @Query("assigned_users") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("dashboard") isDashboard: Boolean? = null,
        @Query("q") query: String? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,
        @Query("epic", encoded = true) epics: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("role", encoded = true) roles: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        // here and below instead of setting header to "false" remove it,
        // because api always returns unpaginated result if header persists, regardless of its value
        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("userstories")
    suspend fun getUserStories(
        @Query("project") project: Long? = null,
        @Query("milestone") sprint: String, //ANY
        @Query("status") status: Long? = null,
        @Query("epic") epic: Long? = null,
        @Query("page") page: Int? = null,
        @Query("assigned_users") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("dashboard") isDashboard: Boolean? = null,
        @Query("q") query: String? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,
        @Query("epic", encoded = true) epics: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("role", encoded = true) roles: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        // here and below instead of setting header to "false" remove it,
        // because api always returns unpaginated result if header persists, regardless of its value
        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("tasks?order_by=us_order")
    suspend fun getTasks(
        @Query("user_story") userStory: String? = null, //ANY
        @Query("project") project: Long? = null,
        @Query("milestone") sprint: Long? = null,
        @Query("page") page: Int? = null,
        @Query("assigned_to") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("tasks?order_by=us_order")
    suspend fun getTasks(
        @Query("user_story") userStory: Long, //ANY
        @Query("project") project: Long? = null,
        @Query("milestone") sprint: Long? = null,
        @Query("page") page: Int? = null,
        @Query("assigned_to") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("epics")
    suspend fun getEpics(
        @Query("page") page: Int? = null,
        @Query("project") project: Long? = null,
        @Query("q") query: String? = null,
        @Query("assigned_to") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("issues")
    suspend fun getIssues(
        @Query("page") page: Int? = null,
        @Query("project") project: Long? = null,
        @Query("q") query: String? = null,
        @Query("milestone") sprint: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("priority", encoded = true) priorities: String? = null,
        @Query("severity", encoded = true) severities: String? = null,
        @Query("type", encoded = true) types: String? = null,
        @Query("role", encoded = true) roles: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("userstories/by_ref")
    suspend fun getUserStoryByRef(
        @Query("project") projectId: Long,
        @Query("ref") ref: Int
    ): CommonTaskResponse

    @GET("{taskPath}/{id}")
    suspend fun getCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long
    ): CommonTaskResponse

    @Headers("Content-Type: application/json")
    @PATCH("{taskPath}/{id}")
    suspend fun editCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body editCommonTaskRequest: EditCommonTaskRequest
    )

    @Headers("Content-Type: application/json")
    @POST("{taskPath}")
    suspend fun createCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Body createRequest: CreateCommonTaskRequest
    ): CommonTaskResponse

    @Headers("Content-Type: application/json")
    @POST("tasks")
    suspend fun createTask(@Body createTaskRequest: CreateTaskRequest): CommonTaskResponse

    @Headers("Content-Type: application/json")
    @POST("issues")
    suspend fun createIssue(@Body createIssueRequest: CreateIssueRequest): CommonTaskResponse

    @Headers("Content-Type: application/json")
    @POST("userstories")
    suspend fun createUserstory(@Body createUserStoryRequest: CreateUserStoryRequest): CommonTaskResponse

    @DELETE("{taskPath}/{id}")
    suspend fun deleteCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long
    ): Response

    @Headers("Content-Type: application/json")
    @POST("epics/{id}/related_userstories")
    suspend fun linkToEpic(
        @Path("id") epicId: Long,
        @Body linkToEpicRequest: LinkToEpicRequest
    )

    @DELETE("epics/{epicId}/related_userstories/{userStoryId}")
    suspend fun unlinkFromEpic(
        @Path("epicId") epicId: Long,
        @Path("userStoryId") userStoryId: Long
    ): Response

    @Headers("Content-Type: application/json")
    @POST("{taskPath}/{id}/promote_to_user_story")
    suspend fun promoteCommonTaskToUserStory(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long,
        @Body promoteToUserStoryRequest: PromoteToUserStoryRequest
    ): List<Int>

    // Tasks comments

    @Headers("Content-Type: application/json")
    @PATCH("{taskPath}/{id}")
    suspend fun createCommonTaskComment(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @GET("history/{taskPath}/{id}?type=comment")
    suspend fun getCommonTaskComments(
        @Path("taskPath") taskPath: CommonTaskPathSingular,
        @Path("id") id: Long
    ): List<Comment>

    @POST("history/{taskPath}/{id}/delete_comment")
    suspend fun deleteCommonTaskComment(
        @Path("taskPath") taskPath: CommonTaskPathSingular,
        @Path("id") id: Long,
        @Query("id") commentId: String
    )

    // Tasks attachments

    @GET("{taskPath}/attachments")
    suspend fun getCommonTaskAttachments(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Query("object_id") storyId: Long,
        @Query("project") projectId: Long
    ): List<Attachment>

    @DELETE("{taskPath}/attachments/{id}")
    suspend fun deleteCommonTaskAttachment(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") attachmentId: Long
    ): Response

    @POST("{taskPath}/attachments")
    @Multipart
    suspend fun uploadCommonTaskAttachment(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Part multipartBody: MultiPartFormDataContent,
    )

    // Custom attributes

    @GET("{taskPath}-custom-attributes")
    suspend fun getCustomAttributes(
        @Path("taskPath") taskPath: CommonTaskPathSingular,
        @Query("project") projectId: Long
    ): List<CustomAttributeResponse>

    @GET("{taskPath}/custom-attributes-values/{id}")
    suspend fun getCustomAttributesValues(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long
    ): CustomAttributesValuesResponse

    @Headers("Content-Type: application/json")
    @PATCH("{taskPath}/custom-attributes-values/{id}")
    suspend fun editCustomAttributesValues(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long,
        @Body editRequest: EditCustomAttributesValuesRequest
    )

    // Swimlanes

    @GET("swimlanes")
    suspend fun getSwimlanes(@Query("project") project: Long): List<Swimlane>


    // Wiki

    @GET("wiki")
    suspend fun getProjectWikiPages(
        @Query("project") projectId: Long
    ): List<WikiPage>

    @GET("wiki/by_slug")
    suspend fun getProjectWikiPageBySlug(
        @Query("project") projectId: Long,
        @Query("slug") slug: String
    ): WikiPage

    @Headers("Content-Type: application/json")
    @PATCH("wiki/{id}")
    suspend fun editWikiPage(
        @Path("id") pageId: Long,
        @Body editWikiPageRequest: EditWikiPageRequest
    )

    @GET("wiki/attachments")
    suspend fun getPageAttachments(
        @Query("object_id") pageId: Long,
        @Query("project") projectId: Long
    ): List<Attachment>

    @POST("wiki/attachments")
    @Multipart
    suspend fun uploadPageAttachment(
        @Part multipartBody: MultiPartFormDataContent
    )

    @DELETE("wiki/attachments/{id}")
    suspend fun deletePageAttachment(
        @Path("id") attachmentId: Long
    ): Response

    @DELETE("wiki/{id}")
    suspend fun deleteWikiPage(
        @Path("id") pageId: Long
    ): Response

    @GET("wiki-links")
    suspend fun getWikiLink(
        @Query("project") projectId: Long
    ): List<WikiLink>

    @Headers("Content-Type: application/json")
    @POST("wiki-links")
    suspend fun createWikiLink(
        @Body newWikiLinkRequest: NewWikiLinkRequest
    )

    @DELETE("wiki-links/{id}")
    suspend fun deleteWikiLink(
        @Path("id") linkId: Long
    ): Response
}
