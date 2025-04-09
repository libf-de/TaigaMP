package de.libf.taigamp.ui.screens.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.FiltersData
import de.libf.taigamp.domain.paging.CommonPagingSource
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.state.subscribeToAll
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IssuesViewModel() : ViewModel(), KoinComponent {
    private val session: Session by inject()
    private val tasksRepository: ITasksRepository by inject()

    val projectName by lazy { session.currentProjectName }

    private var shouldReload = true
    
    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            filters.loadOrError { tasksRepository.getFiltersData(CommonTaskType.Issue) }
            filters.value.data?.let {
                session.changeIssuesFilters(activeFilters.last().updateData(it))
            }
        }
        shouldReload = false
    }

    val filters = MutableResultFlow<FiltersData>()
    val activeFilters by lazy { session.issuesFilters }
    @OptIn(ExperimentalCoroutinesApi::class)
    val issues by lazy {
        activeFilters.flatMapLatest { filters ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE, enablePlaceholders = false)) {
                CommonPagingSource { tasksRepository.getIssues(it, filters) }
            }.flow
        }
    }

    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger = _refreshTrigger.asStateFlow()

    fun selectFilters(filters: FiltersData) {
        viewModelScope.launch { session.changeIssuesFilters(filters) }
    }
    
    init {
        session.currentProjectId.onEach {
            shouldReload = true
        }.launchIn(viewModelScope)

        viewModelScope.subscribeToAll(session.currentProjectId, session.taskEdit) {
            _refreshTrigger.value++;
        }
    }
}
