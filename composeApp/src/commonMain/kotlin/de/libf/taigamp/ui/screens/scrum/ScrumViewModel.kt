package de.libf.taigamp.ui.screens.scrum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.FiltersData
import de.libf.taigamp.domain.paging.CommonPagingSource
import de.libf.taigamp.domain.repositories.ISprintsRepository
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.NothingResult
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.permission_error

class ScrumViewModel() : ViewModel(), KoinComponent {
    private val tasksRepository: ITasksRepository by inject()
    private val sprintsRepository: ISprintsRepository by inject()
    private val session: Session by inject()

    val projectName by lazy { session.currentProjectName }

    private var shouldReload = true

    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            filters.loadOrError {
                tasksRepository.getFiltersData(
                    commonTaskType = CommonTaskType.UserStory,
                    isCommonTaskFromBacklog = true
                )
            }

            filters.value.data?.let {
                session.changeScrumFilters(activeFilters.value.updateData(it))
            }
        }
        shouldReload = false
    }

    // stories

    val filters = MutableResultFlow<FiltersData>()
    val activeFilters by lazy { session.scrumFilters }
    @OptIn(ExperimentalCoroutinesApi::class)
    val stories by lazy {
        activeFilters.flatMapLatest { filters ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE, enablePlaceholders = false)) {
                CommonPagingSource { tasksRepository.getBacklogUserStories(it, filters) }
            }.flow
        }
    }
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger = _refreshTrigger.asStateFlow()

    fun selectFilters(filters: FiltersData) {
        viewModelScope.launch { session.changeScrumFilters(filters) }
    }

    // sprints

    val openSprints by sprints(isClosed = false)
    val closedSprints by sprints(isClosed = true)

    private fun sprints(isClosed: Boolean) = lazy {
        Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
            CommonPagingSource { sprintsRepository.getSprints(it, isClosed) }
        }.flow
    }
    private val _openSprintsRefreshTrigger = MutableStateFlow(0)
    val openSprintsRefreshTrigger = _openSprintsRefreshTrigger.asStateFlow()

    val createSprintResult = MutableResultFlow<Unit>(NothingResult())

    fun createSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        createSprintResult.loadOrError(Res.string.permission_error) {
            sprintsRepository.createSprint(name, start, end)
            _openSprintsRefreshTrigger.value++
            null

//            openSprints.refresh()
        }
    }

    init {
        session.currentProjectId.onEach {
            createSprintResult.value = NothingResult()
            _refreshTrigger.value++
//            stories.refresh()
//            openSprints.refresh()
//            closedSprints.refresh()
            shouldReload = true
        }.launchIn(viewModelScope)

        session.taskEdit.onEach {
            _refreshTrigger.value++
//            stories.refresh()
//            openSprints.refresh()
//            closedSprints.refresh()
        }.launchIn(viewModelScope)

        session.sprintEdit.onEach {
//            openSprints.refresh()
//            closedSprints.refresh()
            _refreshTrigger.value++
        }.launchIn(viewModelScope)
    }
}
