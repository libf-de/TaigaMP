package de.libf.taigamp.ui.screens.projectselector

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavController
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.ui.components.editors.SelectorList
import de.libf.taigamp.ui.components.editors.SelectorListConstants
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.utils.subscribeOnError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.ic_check
import taigamultiplatform.composeapp.generated.resources.more
import taigamultiplatform.composeapp.generated.resources.project_admin
import taigamultiplatform.composeapp.generated.resources.project_member
import taigamultiplatform.composeapp.generated.resources.project_name_template
import taigamultiplatform.composeapp.generated.resources.project_owner
import taigamultiplatform.composeapp.generated.resources.search_projects_hint

@Composable
fun ProjectSelectorScreen(
    navController: NavController,
    showMessage: (StringResource) -> Unit = {},
) {
    val viewModel: ProjectSelectorViewModel = koinViewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }
    val coroutineScope = rememberCoroutineScope()

    val refreshTrigger by viewModel.refreshTrigger.collectAsState()
    val projects = viewModel.projects.collectAsLazyPagingItems()
    projects.subscribeOnError(showMessage)

    LaunchedEffect(refreshTrigger) {
        projects.refresh()
    }

    val currentProjectId by viewModel.currentProjectId.collectAsState(-1)

    var isSelectorVisible by remember { mutableStateOf(true) }
    val selectorAnimationDuration = SelectorListConstants.defaultAnimDurationMillis

    fun navigateBack() = coroutineScope.launch {
        isSelectorVisible = false
        delay(selectorAnimationDuration.toLong())
        navController.popBackStack()
    }

    ProjectSelectorScreenContent(
        projects = projects,
        isVisible = isSelectorVisible,
        currentProjectId = currentProjectId,
        selectorAnimationDuration = selectorAnimationDuration,
        navigateBack = ::navigateBack,
        searchProjects = { viewModel.searchProjects(it) },
        selectProject = {
            viewModel.selectProject(it)
            navigateBack()
        }
    )

}

@Composable
fun ProjectSelectorScreenContent(
    projects: LazyPagingItems<Project>? = null,
    isVisible: Boolean = false,
    currentProjectId: Long = -1,
    selectorAnimationDuration: Int = SelectorListConstants.defaultAnimDurationMillis,
    navigateBack: () -> Unit = {},
    searchProjects: (String) -> Unit = {},
    selectProject: (Project) -> Unit  = {}
) = Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopStart
) {
    if (projects == null) return@Box

    SelectorList(
        titleHintId = Res.string.search_projects_hint,
        itemsLazy = projects,
        isVisible = isVisible,
        searchData = searchProjects,
        navigateBack = navigateBack,
        animationDurationMillis = selectorAnimationDuration
    ) {
        ItemProject(
            project = it,
            currentProjectId = currentProjectId,
            onClick = { selectProject(it) }
        )
    }
}

@Composable
private fun ItemProject(
    project: Project,
    currentProjectId: Long,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.weight(0.8f)) {
            project.takeIf { it.isMember || it.isAdmin || it.isOwner }?.let {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(
                        when {
                            project.isOwner -> Res.string.project_owner
                            project.isAdmin -> Res.string.project_admin
                            project.isMember -> Res.string.project_member
                            else -> Res.string.more //TODO: Find generic string
                        }
                    )
                )
            }

            Text(
                text = stringResource(Res.string.project_name_template,
                    project.name,
                    project.slug
                )
            )
        }

        if (project.id == currentProjectId) {
            Image(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(0.2f)
            )
        }
    }
}


@Preview
@Composable
fun ProjectSelectorScreenPreview() = TaigaMobileTheme {
    ProjectSelectorScreenContent(isVisible = true)
}

