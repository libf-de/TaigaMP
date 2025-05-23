package de.libf.taigamp.ui.screens.commontask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.insertHeaderItem

import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.domain.paging.CommonPagingSource
import de.libf.taigamp.domain.repositories.ISprintsRepository
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.state.postUpdate
import de.libf.taigamp.ui.utils.*
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.permission_error

class CommonTaskViewModel() : ViewModel(), KoinComponent {
    private val session: Session by inject()
    private val tasksRepository: ITasksRepository by inject()
    private val usersRepository: IUsersRepository by inject()
    private val sprintsRepository: ISprintsRepository by inject()

    companion object {
        val SPRINT_HEADER = Sprint(-1, "HEADER", -1, LOCALDATE_MIN, LOCALDATE_MIN, 0, false)
        val SWIMLANE_HEADER = Swimlane(-1, "HEADER", -1)
    }

    private var commonTaskId: Long = -1
    private lateinit var commonTaskType: CommonTaskType

    val commonTask = MutableResultFlow<CommonTaskExtended>()

    val creator = MutableResultFlow<User>()
    val customFields = MutableResultFlow<CustomFields>()
    val attachments = MutableResultFlow<List<Attachment>>()
    val assignees = MutableResultFlow<List<User>>()
    val watchers = MutableResultFlow<List<User>>()
    val userStories = MutableResultFlow<List<CommonTask>>()
    val tasks = MutableResultFlow<List<CommonTask>>()
    val comments = MutableResultFlow<List<Comment>>()

    val team = MutableResultFlow<List<User>>()
    val tags = MutableResultFlow<List<Tag>>()
    val swimlanes = MutableResultFlow<List<Swimlane>>()
    val statuses = MutableResultFlow<Map<StatusType, List<Status>>>()

    val isAssignedToMe = combine(assignees, session.currentUserId) { _assignees, _currentUserId ->
        _currentUserId in _assignees.data?.map { it.id }.orEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isWatchedByMe = combine(watchers, session.currentUserId) { _watchers, _currentUserId ->
        _currentUserId in _watchers.data?.map { it.id }.orEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val projectName by lazy { session.currentProjectName }

    fun onOpen(commonTaskId: Long, commonTaskType: CommonTaskType) {
        this.commonTaskId = commonTaskId
        this.commonTaskType = commonTaskType
        loadData(isReloading = false)
    }

    private fun loadData(isReloading: Boolean = true) = viewModelScope.launch {
        commonTask.loadOrError(showLoading = !isReloading) {
            tasksRepository.getCommonTask(commonTaskId, commonTaskType).also {

                suspend fun MutableResultFlow<List<User>>.loadUsersFromIds(ids: List<Long>) =
                    loadOrError(showLoading = false) {
                        coroutineScope {
                            ids.map {
                                async { usersRepository.getUser(it) }
                            }.awaitAll()
                        }
                    }

                val jobsToLoad = arrayOf(
                    launch {
                        creator.loadOrError(showLoading = false) { usersRepository.getUser(it.creatorId) }
                    },
                    launch {
                        customFields.loadOrError(showLoading = false) { tasksRepository.getCustomFields(commonTaskId, commonTaskType) }
                    },
                    launch {
                        attachments.loadOrError(showLoading = false) { tasksRepository.getAttachments(commonTaskId, commonTaskType) }
                    },
                    launch { assignees.loadUsersFromIds(it.assignedIds) },
                    launch { watchers.loadUsersFromIds(it.watcherIds) },
                    launch {
                        userStories.loadOrError(showLoading = false) { tasksRepository.getEpicUserStories(commonTaskId) }
                    },
                    launch {
                        tasks.loadOrError(showLoading = false) { tasksRepository.getUserStoryTasks(commonTaskId) }
                    },
                    launch {
                        comments.loadOrError(showLoading = false) { tasksRepository.getComments(commonTaskId, commonTaskType) }
                    },
                    launch {
                        tags.loadOrError(showLoading = false) {
                            tasksRepository.getAllTags(commonTaskType).also { tagsSearched.value = it }
                        }
                    }
                ) + if (!isReloading) {
                    arrayOf(
                        launch {
                            team.loadOrError(showLoading = false) {
                                usersRepository.getTeam()
                                    .map { it.toUser() }
                                    .also { teamSearched.value = it }
                            }
                        },
                        launch {
                            swimlanes.loadOrError(showLoading = false) {
                                listOf(SWIMLANE_HEADER) + tasksRepository.getSwimlanes() // prepend "unclassified"
                            }
                        },
                        launch {
                            statuses.loadOrError(showLoading = false) {
                                StatusType.values().filter {
                                    if (commonTaskType != CommonTaskType.Issue) it == StatusType.Status else true
                                }.associateWith { tasksRepository.getStatusByType(commonTaskType, it) }
                            }
                        }
                    )
                } else {
                    emptyArray()
                }

                joinAll(*jobsToLoad)
            }
        }
    }

    // ================
    // Edit task itself
    // ================

    // Edit task itself (title & description)
    val editBasicInfoResult = MutableResultFlow<Unit>()

    fun editBasicInfo(title: String, description: String) = viewModelScope.launch {
        editBasicInfoResult.loadOrError(Res.string.permission_error) {
            tasksRepository.editCommonTaskBasicInfo(commonTask.value.data!!, title, description)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    // Edit status (and also type, severity, priority)
    val editStatusResult = MutableResultFlow<StatusType>()

    fun editStatus(status: Status) = viewModelScope.launch {
        editStatusResult.value = LoadingResult(status.type)

        editStatusResult.loadOrError(Res.string.permission_error) {
            tasksRepository.editStatus(commonTask.value.data!!, status.id, status.type)
            loadData().join()
            session.taskEdit.postUpdate()
            status.type
        }
    }

    // Edit sprint
    val sprints by lazy {
        Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
            CommonPagingSource { sprintsRepository.getSprints(it) }
        }.flow.map { it.insertHeaderItem(item = SPRINT_HEADER) }
    }// prepend "Move to backlog"


    val editSprintResult = MutableResultFlow<Unit>(NothingResult())

    fun editSprint(sprint: Sprint) = viewModelScope.launch {
        editSprintResult.loadOrError(Res.string.permission_error) {
            tasksRepository.editSprint(commonTask.value.data!!, sprint.takeIf { it != SPRINT_HEADER }?.id)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    // use team for both assignees and watchers
    val teamSearched = MutableStateFlow(emptyList<User>())

    fun searchTeam(query: String) = viewModelScope.launch {
        val q = query.lowercase()
        teamSearched.value = team.value.data
            .orEmpty()
            .filter { q in it.username.lowercase() || q in it.displayName.lowercase() }
    }

    // Edit assignees

    private fun editAssignees(userId: Long, remove: Boolean) = viewModelScope.launch {
        assignees.loadOrError(Res.string.permission_error) {
            teamSearched.value = team.value.data.orEmpty()

            tasksRepository.editAssignees(
                commonTask.value.data!!,
                commonTask.value.data!!.assignedIds.let {
                    if (remove) it - userId
                    else it + userId
                }
            )

            loadData().join()
            session.taskEdit.postUpdate()
            assignees.value.data
        }
    }

    fun addAssignee(userId: Long? = null) = viewModelScope.launch { editAssignees(userId ?: session.currentUserId.value, remove = false) }
    fun removeAssignee(userId: Long? = null) = viewModelScope.launch { editAssignees(userId ?: session.currentUserId.value, remove = true) }

    // Edit watchers

    private fun editWatchers(userId: Long, remove: Boolean) = viewModelScope.launch {
        watchers.loadOrError(Res.string.permission_error) {
            teamSearched.value = team.value.data.orEmpty()

            tasksRepository.editWatchers(
                commonTask.value.data!!,
                commonTask.value.data?.watcherIds.orEmpty().let {
                    if (remove) it - userId
                    else it + userId
                }
            )

            loadData().join()
            session.taskEdit.postUpdate()
            watchers.value.data
        }
    }

    fun addWatcher(userId: Long? = null) = viewModelScope.launch { editWatchers(userId ?: session.currentUserId.value, remove = false) }
    fun removeWatcher(userId: Long? = null) = viewModelScope.launch { editWatchers(userId ?: session.currentUserId.value, remove = true) }

    // Tags
    val tagsSearched = MutableStateFlow(emptyList<Tag>())

    fun searchTags(query: String) = viewModelScope.launch {
        tagsSearched.value = tags.value.data.orEmpty().filter { query.isNotEmpty() && query.lowercase() in it.name }
    }

    private fun editTag(tag: Tag, remove: Boolean) = viewModelScope.launch {
        tags.loadOrError(Res.string.permission_error) {
            tagsSearched.value = tags.value.data.orEmpty()

            tasksRepository.editTags(
                commonTask.value.data!!,
                commonTask.value.data!!.tags.let { if (remove) it - tag else it + tag },
            )

            loadData().join()
            session.taskEdit.postUpdate()
            tags.value.data
        }
    }

    fun addTag(tag: Tag) = editTag(tag, remove = false)
    fun deleteTag(tag: Tag) = editTag(tag, remove = true)

    // Swimlanes
    fun editSwimlane(swimlane: Swimlane) = viewModelScope.launch {
        swimlanes.loadOrError(Res.string.permission_error) {
            tasksRepository.editUserStorySwimlane(commonTask.value.data!!, swimlane.takeIf { it != SWIMLANE_HEADER }?.id)
            loadData().join()
            session.taskEdit.postUpdate()
            swimlanes.value.data
        }
    }

    // Due date
    val editDueDateResult = MutableResultFlow<Unit>()

    fun editDueDate(date: LocalDate?) = viewModelScope.launch {
        editDueDateResult.loadOrError(Res.string.permission_error) {
            tasksRepository.editDueDate(commonTask.value.data!!, date)
            loadData().join()
        }
    }

    // Epic color
    val editEpicColorResult = MutableResultFlow<Unit>()

    fun editEpicColor(color: String) = viewModelScope.launch {
        editEpicColorResult.loadOrError(Res.string.permission_error) {
            tasksRepository.editEpicColor(commonTask.value.data!!, color)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    val editBlockedResult = MutableResultFlow<Unit>()

    fun editBlocked(blockedNote: String?) = viewModelScope.launch {
        editBlockedResult.loadOrError(Res.string.permission_error) {
            tasksRepository.editBlocked(commonTask.value.data!!, blockedNote)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    // =============
    // Related edits
    // =============


    // Edit linked epic
    private val epicsQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val epics by lazy {
        epicsQuery.flatMapLatest { query ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
                CommonPagingSource { tasksRepository.getEpics(it, FiltersData(query = query)) }
            }.flow
        }
    }

    fun searchEpics(query: String) {
        epicsQuery.value = query
    }

    val linkToEpicResult = MutableResultFlow<Unit>(NothingResult())

    fun linkToEpic(epic: CommonTask) = viewModelScope.launch {
        linkToEpicResult.loadOrError(Res.string.permission_error) {
            tasksRepository.linkToEpic(epic.id, commonTaskId)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    fun unlinkFromEpic(epic: EpicShortInfo) = viewModelScope.launch {
        linkToEpicResult.loadOrError(Res.string.permission_error) {
            tasksRepository.unlinkFromEpic(epic.id, commonTaskId)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    // Edit comments

    fun createComment(comment: String) = viewModelScope.launch {
        comments.loadOrError(Res.string.permission_error) {
            tasksRepository.createComment(commonTaskId, commonTaskType, comment, commonTask.value.data!!.version)
            loadData().join()
            comments.value.data
        }
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        comments.loadOrError(Res.string.permission_error) {
            tasksRepository.deleteComment(commonTaskId, commonTaskType, comment.id)
            loadData().join()
            comments.value.data
        }
    }


    fun deleteAttachment(attachment: Attachment) = viewModelScope.launch {
        attachments.loadOrError(Res.string.permission_error) {
            tasksRepository.deleteAttachment(commonTaskType, attachment.id)
            loadData().join()
            attachments.value.data
        }
    }

    fun addAttachment(fileName: String, inputStream: ByteReadChannel) = viewModelScope.launch {
        attachments.loadOrError(Res.string.permission_error) {
            tasksRepository.addAttachment(commonTaskId, commonTaskType, fileName, inputStream)
            loadData().join()
            attachments.value.data
        }
    }

    // Delete task
    val deleteResult = MutableResultFlow<Unit>()

    fun deleteTask() = viewModelScope.launch {
        deleteResult.loadOrError(Res.string.permission_error) {
            tasksRepository.deleteCommonTask(commonTaskType, commonTaskId)
            session.taskEdit.postUpdate()
        }
    }

    val promoteResult = MutableResultFlow<CommonTask>()

    fun promoteToUserStory() = viewModelScope.launch {
        promoteResult.loadOrError(Res.string.permission_error, preserveValue = false) {
            tasksRepository.promoteCommonTaskToUserStory(commonTaskId, commonTaskType).also {
                session.taskEdit.postUpdate()
            }
        }
    }

    fun editCustomField(customField: CustomField, value: CustomFieldValue?) = viewModelScope.launch {
        customFields.loadOrError(Res.string.permission_error) {
            tasksRepository.editCustomFields(
                commonTaskType = commonTaskType,
                commonTaskId = commonTaskId,
                fields = customFields.value.data?.fields.orEmpty().map {
                    it.id to (if (it.id == customField.id) value else it.value)
                }.toMap(),
                version = customFields.value.data?.version ?: 0
            )
            loadData().join()
            customFields.value.data
        }

    }
}
