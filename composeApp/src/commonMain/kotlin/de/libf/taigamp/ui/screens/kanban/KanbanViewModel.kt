package de.libf.taigamp.ui.screens.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.state.subscribeToAll
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.NothingResult
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class KanbanViewModel(
    private val tasksRepository: ITasksRepository,
    private val usersRepository: IUsersRepository,
    private val session: Session
) : ViewModel() {
    val projectName by lazy { session.currentProjectName }

    val statuses = MutableResultFlow<List<Status>>()
    val team = MutableResultFlow<List<User>>()
    val stories = MutableResultFlow<List<CommonTaskExtended>>()
    val swimlanes = MutableResultFlow<List<Swimlane?>>()

    val selectedSwimlane = MutableStateFlow<Swimlane?>(null)

    private var shouldReload = true

    fun onOpen() = viewModelScope.launch {
        if (!shouldReload) return@launch
        joinAll(
            launch {
                statuses.loadOrError(preserveValue = false) { tasksRepository.getStatuses(CommonTaskType.UserStory) }
            },
            launch {
                team.loadOrError(preserveValue = false) { usersRepository.getTeam().map { it.toUser() } }
            },
            launch {
                stories.loadOrError(preserveValue = false) { tasksRepository.getAllUserStories() }
            },
            launch {
                swimlanes.loadOrError {
                    listOf(null) + tasksRepository.getSwimlanes() // prepend null to show "unclassified" swimlane
                }
            }
        )
        shouldReload = false
    }

    fun selectSwimlane(swimlane: Swimlane?) {
        selectedSwimlane.value = swimlane
    }

    init {
        viewModelScope.subscribeToAll(session.currentProjectId, session.taskEdit) {
            statuses.value = NothingResult()
            team.value = NothingResult()
            stories.value = NothingResult()
            swimlanes.value = NothingResult()
            shouldReload = true
        }
    }
}
