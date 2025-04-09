package de.libf.taigamp.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.components.containers.HorizontalTabbedPager
import de.libf.taigamp.ui.components.containers.Tab
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.components.lists.ProjectCard
import de.libf.taigamp.ui.components.lists.SimpleTasksListWithTitle
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.theme.*
import de.libf.taigamp.ui.utils.navigateToTaskScreen
import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.dashboard
import taigamultiplatform.composeapp.generated.resources.my_projects
import taigamultiplatform.composeapp.generated.resources.watching
import taigamultiplatform.composeapp.generated.resources.working_on

@Composable
fun DashboardScreen(
    navController: NavController,
    showMessage: (message: StringResource) -> Unit = {},
) {
    val viewModel: DashboardViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val workingOn by viewModel.workingOn.collectAsState()
    workingOn.subscribeOnError(showMessage)

    val watching by viewModel.watching.collectAsState()
    watching.subscribeOnError(showMessage)

    val myProjects by viewModel.myProjects.collectAsState()
    myProjects.subscribeOnError(showMessage)

    val currentProjectId by viewModel.currentProjectId.collectAsState(-1)

    DashboardScreenContent(
        isLoading = listOf(workingOn, watching, myProjects).any { it is LoadingResult<*> },
        workingOn = workingOn.data.orEmpty(),
        watching = watching.data.orEmpty(),
        myProjects = myProjects.data.orEmpty(),
        currentProjectId = currentProjectId,
        navigateToTask = {
            viewModel.changeCurrentProject(it.projectInfo)
            navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
        },
        changeCurrentProject = viewModel::changeCurrentProject
    )
}

@Composable
fun DashboardScreenContent(
    isLoading: Boolean = false,
    workingOn: List<CommonTask> = emptyList(),
    watching: List<CommonTask> = emptyList(),
    myProjects: List<Project> = emptyList(),
    currentProjectId: Long = 0,
    navigateToTask: (CommonTask) -> Unit = { _ -> },
    changeCurrentProject: (Project) -> Unit = { _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    AppBarWithBackButton(title = { Text(stringResource(Res.string.dashboard)) })

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }
    } else {
        HorizontalTabbedPager(
            tabs = Tabs.values(),
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (Tabs.values()[page]) {
                Tabs.WorkingOn -> TabContent(
                    commonTasks = workingOn,
                    navigateToTask = navigateToTask
                )
                Tabs.Watching -> TabContent(
                    commonTasks = watching,
                    navigateToTask = navigateToTask
                )
                Tabs.MyProjects -> MyProjects(
                    myProjects = myProjects,
                    currentProjectId = currentProjectId,
                    changeCurrentProject = changeCurrentProject
                )
            }
        }
    }

}

private enum class Tabs(override val titleId: StringResource) : Tab {
    WorkingOn(Res.string.working_on),
    Watching(Res.string.watching),
    MyProjects(Res.string.my_projects)
}

@Composable
private fun TabContent(
    commonTasks: List<CommonTask>,
    navigateToTask: (CommonTask) -> Unit,
) = LazyColumn(Modifier.fillMaxSize()) {
    SimpleTasksListWithTitle(
        bottomPadding = commonVerticalPadding,
        horizontalPadding = mainHorizontalScreenPadding,
        showExtendedTaskInfo = true,
        commonTasks = commonTasks,
        navigateToTask = { id, _, _ -> navigateToTask(commonTasks.find { it.id == id }!!) },
    )
}

@Composable
private fun MyProjects(
    myProjects: List<Project>,
    currentProjectId: Long,
    changeCurrentProject: (Project) -> Unit
) = LazyColumn {
    items(myProjects) {
        ProjectCard(
            project = it,
            isCurrent = it.id == currentProjectId,
            onClick = { changeCurrentProject(it) }
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Preview
@Composable
private fun ProjectCardPreview() = TaigaMobileTheme {
    ProjectCard(
        project = Project(
            id = 0,
            name = "Name",
            slug = "slug",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            isPrivate = true
        ),
        isCurrent = true,
        onClick = {}
    )
}

@Preview
@Composable
private fun DashboardPreview() = TaigaMobileTheme {
    DashboardScreenContent(
        myProjects = List(3) {
            Project(
                id = it.toLong(),
                name = "Name",
                slug = "slug",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                isPrivate = true
            )
        }
    )
}
