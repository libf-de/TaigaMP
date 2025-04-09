package de.libf.taigamp.ui.screens.issues

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.FiltersData
import de.libf.taigamp.ui.components.TasksFiltersWithLazyList
import de.libf.taigamp.ui.components.buttons.PlusButton
import de.libf.taigamp.ui.components.appbars.ClickableAppBar
import de.libf.taigamp.ui.components.lists.SimpleTasksListWithTitle
import de.libf.taigamp.ui.screens.main.Routes
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.commonVerticalPadding
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun IssuesScreen(
    navController: NavController,
    showMessage: (message: StringResource) -> Unit = {}
) {
    val viewModel: IssuesViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState("")

    val refreshTrigger by viewModel.refreshTrigger.collectAsState()
    val issues = viewModel.issues.collectAsLazyPagingItems()
    issues.subscribeOnError(showMessage)

    val filters by viewModel.filters.collectAsState()
    filters.subscribeOnError(showMessage)

    val activeFilters by viewModel.activeFilters.collectAsState(FiltersData())

    LaunchedEffect(refreshTrigger) {
        issues.refresh()
    }

    IssuesScreenContent(
        projectName = projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Issue) },
        issues = issues,
        filters = filters.data ?: FiltersData(),
        activeFilters = activeFilters,
        selectFilters = viewModel::selectFilters,
        navigateToTask = navController::navigateToTaskScreen
    )
}

@Composable
fun IssuesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    issues: LazyPagingItems<CommonTask>? = null,
    filters: FiltersData = FiltersData(),
    activeFilters: FiltersData = FiltersData(),
    selectFilters: (FiltersData) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ClickableAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    TasksFiltersWithLazyList(
        filters = filters,
        activeFilters = activeFilters,
        selectFilters = selectFilters
    ) {
        SimpleTasksListWithTitle(
            commonTasksLazy = issues,
            keysHash = activeFilters.hashCode(),
            navigateToTask = navigateToTask,
            horizontalPadding = mainHorizontalScreenPadding,
            bottomPadding = commonVerticalPadding
        )
    }
}

@Preview
@Composable
fun IssuesScreenPreview() = TaigaMobileTheme {
    IssuesScreenContent(
        projectName = "Cool project"
    )
}
