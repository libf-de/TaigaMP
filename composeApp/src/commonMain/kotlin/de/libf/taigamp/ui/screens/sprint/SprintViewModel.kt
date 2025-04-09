package de.libf.taigamp.ui.screens.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.Sprint
import de.libf.taigamp.domain.entities.Status
import de.libf.taigamp.domain.repositories.ISprintsRepository
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.state.Session
import de.libf.taigamp.state.postUpdate
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.NothingResult
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.permission_error

class SprintViewModel(
    private val tasksRepository: ITasksRepository,
    private val sprintsRepository: ISprintsRepository,
    private val session: Session
) : ViewModel() {
    private var sprintId: Long = -1

    val sprint = MutableResultFlow<Sprint>()
    val statuses = MutableResultFlow<List<Status>>()
    val storiesWithTasks = MutableResultFlow<Map<CommonTask, List<CommonTask>>>()
    val storylessTasks = MutableResultFlow<List<CommonTask>>()
    val issues = MutableResultFlow<List<CommonTask>>()

    private var shouldReload = true

    fun onOpen(sprintId: Long) {
        if (!shouldReload) return
        this.sprintId = sprintId
        loadData(isReloading = false)
        shouldReload = false
    }

    private fun loadData(isReloading: Boolean = true) = viewModelScope.launch {
        sprint.loadOrError(showLoading = !isReloading) {
            sprintsRepository.getSprint(sprintId).also {
                joinAll(
                    launch {
                        statuses.loadOrError(showLoading = false) { tasksRepository.getStatuses(CommonTaskType.Task) }
                    },
                    launch {
                        storiesWithTasks.loadOrError(showLoading = false) {
                            coroutineScope {
                                sprintsRepository.getSprintUserStories(sprintId)
                                    .map { it to async { tasksRepository.getUserStoryTasks(it.id) } }
                                    .associate { (story, tasks) -> story to tasks.await() }
                            }
                        }
                    },
                    launch {
                        issues.loadOrError(showLoading = false) { sprintsRepository.getSprintIssues(sprintId) }
                    },
                    launch {
                        storylessTasks.loadOrError(showLoading = false) { sprintsRepository.getSprintTasks(sprintId) }
                    }
                )
            }
        }
    }

    val editResult = MutableResultFlow<Unit>()
    fun editSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        editResult.loadOrError(Res.string.permission_error) {
            sprintsRepository.editSprint(sprintId, name, start, end)
            session.sprintEdit.postUpdate()
            loadData().join()
        }
    }

    val deleteResult = MutableResultFlow<Unit>()
    fun deleteSprint() = viewModelScope.launch {
        deleteResult.loadOrError(Res.string.permission_error) {
            sprintsRepository.deleteSprint(sprintId)
            session.sprintEdit.postUpdate()
        }
    }

    init {
        session.taskEdit.onEach {
            sprintId = -1
            sprint.value = NothingResult()
            statuses.value = NothingResult()
            storiesWithTasks.value = NothingResult()
            storylessTasks.value = NothingResult()
            issues.value = NothingResult()
            shouldReload = true
        }.launchIn(viewModelScope)
    }
}
