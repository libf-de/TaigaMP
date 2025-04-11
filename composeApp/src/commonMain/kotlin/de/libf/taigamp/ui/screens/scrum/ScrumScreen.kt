package de.libf.taigamp.ui.screens.scrum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.domain.entities.Sprint
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.FiltersData
import de.libf.taigamp.ui.components.TasksFiltersWithLazyList
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.components.appbars.ClickableAppBar
import de.libf.taigamp.ui.components.buttons.PlusButton
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.components.containers.HorizontalTabbedPager
import de.libf.taigamp.ui.components.containers.Tab
import de.libf.taigamp.ui.components.dialogs.EditSprintDialog
import de.libf.taigamp.ui.components.dialogs.LoadingDialog
import de.libf.taigamp.ui.components.lists.SimpleTasksListWithTitle
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.components.texts.NothingToSeeHereText
import de.libf.taigamp.ui.screens.main.Routes
import de.libf.taigamp.ui.theme.commonVerticalPadding
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.backlog
import taigamultiplatform.composeapp.generated.resources.closed
import taigamultiplatform.composeapp.generated.resources.common_error_message
import taigamultiplatform.composeapp.generated.resources.hide_closed_sprints
import taigamultiplatform.composeapp.generated.resources.show_closed_sprints
import taigamultiplatform.composeapp.generated.resources.sprint_dates_template
import taigamultiplatform.composeapp.generated.resources.sprints_title
import taigamultiplatform.composeapp.generated.resources.stories_count_template
import taigamultiplatform.composeapp.generated.resources.taskboard

@Composable
fun ScrumScreen(
    navController: NavController,
    showMessage: (message: StringResource) -> Unit = {},
) {
    val viewModel: ScrumViewModel = koinViewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState("")

    val stories = viewModel.stories.collectAsLazyPagingItems()
    stories.subscribeOnError {
        showMessage(Res.string.common_error_message)
    }

    val openSprintsRefreshTrigger by viewModel.openSprintsRefreshTrigger.collectAsState()
    val openSprints = viewModel.openSprints.collectAsLazyPagingItems()
    openSprints.subscribeOnError(showMessage)

    LaunchedEffect(openSprintsRefreshTrigger) {
        openSprints.refresh()
    }

    val closedSprints = viewModel.closedSprints.collectAsLazyPagingItems()
    closedSprints.subscribeOnError(showMessage)

    val createSprintResult by viewModel.createSprintResult.collectAsState()
    createSprintResult.subscribeOnError(showMessage)

    val filters by viewModel.filters.collectAsState()
    filters.subscribeOnError(showMessage)

    val activeFilters by viewModel.activeFilters.collectAsState(FiltersData())

    val refreshTrigger by viewModel.refreshTrigger.collectAsState()
    LaunchedEffect(refreshTrigger) {
        stories.refresh()
        openSprints.refresh()
        closedSprints.refresh()
    }

    ScrumScreenContent(
        projectName = projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        stories = stories,
        filters = filters.data ?: FiltersData(),
        activeFilters = activeFilters,
        selectFilters = viewModel::selectFilters,
        openSprints = openSprints,
        closedSprints = closedSprints,
        isCreateSprintLoading = createSprintResult is LoadingResult,
        navigateToBoard = {
            navController.navigateToSprint(it.id)
        },
        navigateBack = navController::popBackStack,
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.UserStory) },
        createSprint = viewModel::createSprint
    )
}

@Composable
fun ScrumScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    stories: LazyPagingItems<CommonTask>? = null,
    filters: FiltersData = FiltersData(),
    activeFilters: FiltersData = FiltersData(),
    selectFilters: (FiltersData) -> Unit = {},
    openSprints: LazyPagingItems<Sprint>? = null,
    closedSprints: LazyPagingItems<Sprint>? = null,
    isCreateSprintLoading: Boolean = false,
    navigateToBoard: (Sprint) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: () -> Unit = {},
    navigateBack: () -> Unit = {},
    createSprint: (name: String, start: LocalDate, end: LocalDate) -> Unit = { _, _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val pagerState = rememberPagerState { Tabs.values().size }
    var isCreateSprintDialogVisible by remember { mutableStateOf(false) }

    ClickableAppBar(
        projectName = projectName,
        actions = {
            PlusButton(
                onClick = when (Tabs.values()[pagerState.currentPage]) {
                    Tabs.Backlog -> navigateToCreateTask
                    Tabs.Sprints -> { { isCreateSprintDialogVisible = true } }
                }
            )
        },
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )

    if (isCreateSprintDialogVisible) {
        EditSprintDialog(
            onConfirm = { name, start, end ->
                createSprint(name, start, end)
                isCreateSprintDialogVisible = false
            },
            onDismiss = { isCreateSprintDialogVisible = false }
        )
    }

    if (isCreateSprintLoading) {
        LoadingDialog()
    }

    HorizontalTabbedPager(
        tabs = Tabs.values(),
        modifier = Modifier.fillMaxSize(),
        pagerState = pagerState
    ) { page ->
        when (Tabs.values()[page]) {
            Tabs.Backlog -> BacklogTabContent(
                stories = stories,
                filters = filters,
                activeFilters = activeFilters,
                selectFilters = selectFilters,
                navigateToTask = navigateToTask
            )
            Tabs.Sprints -> SprintsTabContent(
                openSprints = openSprints,
                closedSprints = closedSprints,
                navigateToBoard = navigateToBoard
            )
        }
    }

}

private enum class Tabs(override val titleId: StringResource) : Tab {
    Backlog(Res.string.backlog),
    Sprints(Res.string.sprints_title)
}

@Composable
private fun BacklogTabContent(
    stories: LazyPagingItems<CommonTask>?,
    filters: FiltersData = FiltersData(),
    activeFilters: FiltersData = FiltersData(),
    selectFilters: (FiltersData) -> Unit = {},
    navigateToTask: NavigateToTask
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
) {
    TasksFiltersWithLazyList(
        filters = filters,
        activeFilters = activeFilters,
        selectFilters = selectFilters
    ) {
        SimpleTasksListWithTitle(
            commonTasksLazy = stories,
            keysHash = activeFilters.hashCode(),
            navigateToTask = navigateToTask,
            horizontalPadding = mainHorizontalScreenPadding,
            bottomPadding = commonVerticalPadding
        )
    }
}

@Composable
private fun SprintsTabContent(
    openSprints: LazyPagingItems<Sprint>?,
    closedSprints: LazyPagingItems<Sprint>?,
    navigateToBoard: (Sprint) -> Unit,
) {
    if (openSprints == null || closedSprints == null) return

    var isClosedSprintsVisible by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(openSprints.itemSnapshotList.items, key = { it.id }) {
            if (it == null) return@items
            SprintItem(
                sprint = it,
                navigateToBoard = navigateToBoard
            )
        }
//        items(openSprints, key = { it.id }) {
//            if (it == null) return@items
//            SprintItem(
//                sprint = it,
//                navigateToBoard = navigateToBoard
//            )
//        }

        item {
            if (openSprints.loadState.refresh is LoadState.Loading || openSprints.loadState.append is LoadState.Loading) {
                DotsLoader()
            }
        }

        item {
            FilledTonalButton(onClick = { isClosedSprintsVisible = !isClosedSprintsVisible }) {
                Text(stringResource(if (isClosedSprintsVisible) Res.string.hide_closed_sprints else Res.string.show_closed_sprints))
            }
        }

        if (isClosedSprintsVisible) {
//            items(closedSprints, key = { it.id }) {
//                if (it == null) return@items
//                SprintItem(
//                    sprint = it,
//                    navigateToBoard = navigateToBoard
//                )
//            }
            items(closedSprints.itemSnapshotList.items, key = { it.id }) {
                if (it == null) return@items
                SprintItem(
                    sprint = it,
                    navigateToBoard = navigateToBoard
                )
            }

            item {
                if (closedSprints.loadState.refresh is LoadState.Loading || closedSprints.loadState.append is LoadState.Loading) {
                    DotsLoader()
                }
            }
        }

        item {
            if (openSprints.itemCount == 0 && closedSprints.itemCount == 0) {
                NothingToSeeHereText()
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars).padding(bottom = 8.dp))
        }
    }
}

@Composable
private fun SprintItem(
    sprint: Sprint,
    navigateToBoard: (Sprint) -> Unit = {}
) = ContainerBox {
    val dateFormatter = remember { LocalDate.Formats.ISO }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.weight(0.7f)) {
            Text(
                text = sprint.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                stringResource(Res.string.sprint_dates_template,
                    sprint.start.format(dateFormatter),
                    sprint.end.format(dateFormatter)
                )
            )

            Row {
                Text(
                    text = stringResource(Res.string.stories_count_template, sprint.storiesCount),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.width(6.dp))

                if (sprint.isClosed) {
                    Text(
                        text = stringResource(Res.string.closed),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        buttonColors().let {
            val containerColor = if (!sprint.isClosed) it.containerColor else it.disabledContainerColor
            val contentColor = if (!sprint.isClosed) it.contentColor else it.disabledContentColor
//            val containerColor = it.containerColor(!sprint.isClosed)
//            val contentColor = it.contentColor(!sprint.isClosed)

            Button(
                onClick = { navigateToBoard(sprint) },
                modifier = Modifier.weight(0.3f),
                colors = buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            ) {
                Text(stringResource(Res.string.taskboard))
            }
        }
    }
}

@Preview
@Composable
fun SprintPreview() = TaigaMobileTheme {
    SprintItem(
        Sprint(
            id = 0L,
            name = "1 sprint",
            order = 0,
            start = LocalDate.now(),
            end = LocalDate.now(),
            storiesCount = 4,
            isClosed = true
        )
    )
}

@Preview
@Composable
fun ScrumScreenPreview() = TaigaMobileTheme {
    ScrumScreenContent("Lol")
}
