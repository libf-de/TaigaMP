package de.libf.taigamp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.domain.repositories.IProjectsRepository
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.NothingResult
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DashboardViewModel() : ViewModel(), KoinComponent {
    private val tasksRepository: ITasksRepository by inject()
    private val projectsRepository: IProjectsRepository by inject()
    private val session: Session by inject()

    val workingOn = MutableResultFlow<List<CommonTask>>()
    val watching = MutableResultFlow<List<CommonTask>>()
    val myProjects = MutableResultFlow<List<Project>>()

    val currentProjectId by lazy { session.currentProjectId }

    private var shouldReload = true

    fun onOpen() = viewModelScope.launch {
        if (!shouldReload) return@launch
        joinAll(
            launch { workingOn.loadOrError(preserveValue = false) { tasksRepository.getWorkingOn() } },
            launch { watching.loadOrError(preserveValue = false) { tasksRepository.getWatching() } },
            launch { myProjects.loadOrError(preserveValue = false) { projectsRepository.getMyProjects() } }
        )
        shouldReload = false
    }

    fun changeCurrentProject(project: Project) {
        viewModelScope.launch {
            project.apply {
                session.changeCurrentProject(id, name)
            }
        }
    }

    init {
        session.taskEdit.onEach {
            workingOn.value = NothingResult()
            watching.value = NothingResult()
            myProjects.value = NothingResult()
            shouldReload = true
        }.launchIn(viewModelScope)
    }
}
