package de.libf.taigamp.ui.screens.projectselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.domain.paging.CommonPagingSource
import de.libf.taigamp.domain.repositories.IProjectsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProjectSelectorViewModel() : ViewModel(), KoinComponent {
    private val projectsRepository: IProjectsRepository by inject()
    private val session: Session by inject()

    val currentProjectId by lazy { session.currentProjectId }

    fun onOpen() {
        _refreshTrigger.value++
    }

    private val projectsQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val projects by lazy {
        projectsQuery.flatMapLatest { query ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
                CommonPagingSource { projectsRepository.searchProjects(query, it) }
            }.flow
        }
    }
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger = _refreshTrigger.asStateFlow()

    fun searchProjects(query: String) {
        projectsQuery.value = query
    }

    fun selectProject(project: Project) {
        viewModelScope.launch { session.changeCurrentProject(project.id, project.name) }
    }
}
