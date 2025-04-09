package de.libf.taigamp.ui.screens.epics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.FiltersData
import de.libf.taigamp.domain.paging.CommonPagingSource
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.state.Session
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

class EpicsViewModel() : ViewModel(), KoinComponent {
    private val session: Session by inject()
    private val tasksRepository: ITasksRepository by inject()

    val projectName by lazy { session.currentProjectName }

    private var shouldReload = true
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger = _refreshTrigger.asStateFlow()


    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            filters.loadOrError { tasksRepository.getFiltersData(CommonTaskType.Epic) }
            filters.value.data?.let {
                session.changeEpicsFilters(activeFilters.last().updateData(it))
            }
        }
        shouldReload = false
    }

    val filters = MutableResultFlow<FiltersData>()
    val activeFilters by lazy { session.epicsFilters }
    @OptIn(ExperimentalCoroutinesApi::class)
    val epics by lazy {
        activeFilters.flatMapLatest { filters ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE, enablePlaceholders = false)) {
                CommonPagingSource { tasksRepository.getEpics(it, filters) }
            }.flow
        }
    }

    fun selectFilters(filters: FiltersData) {
        viewModelScope.launch {
            session.changeEpicsFilters(filters)
        }
    }

    init {
        session.currentProjectId.onEach {
            _refreshTrigger.value++;
            shouldReload = true
        }.launchIn(viewModelScope)

        session.taskEdit.onEach {
            _refreshTrigger.value++;
        }.launchIn(viewModelScope)
    }
}
