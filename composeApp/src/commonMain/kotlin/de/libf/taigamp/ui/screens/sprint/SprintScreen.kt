package de.libf.taigamp.ui.screens.sprint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.libf.taigamp.domain.entities.Sprint
import de.libf.taigamp.domain.entities.Status
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.utils.SuccessResult
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.components.dialogs.EditSprintDialog
import de.libf.taigamp.ui.components.dialogs.LoadingDialog
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.dialogTonalElevation
import de.libf.taigamp.ui.utils.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.delete
import taigamultiplatform.composeapp.generated.resources.delete_sprint_text
import taigamultiplatform.composeapp.generated.resources.delete_sprint_title
import taigamultiplatform.composeapp.generated.resources.edit
import taigamultiplatform.composeapp.generated.resources.ic_delete
import taigamultiplatform.composeapp.generated.resources.ic_options
import taigamultiplatform.composeapp.generated.resources.sprint_dates_template

@Composable
fun SprintScreen(
    navController: NavController,
    sprintId: Long,
    showMessage: (StringResource) -> Unit = {},
) {
    val viewModel: SprintViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen(sprintId)
    }

    val sprint by viewModel.sprint.collectAsState()
    sprint.subscribeOnError(showMessage)

    val statuses by viewModel.statuses.collectAsState()
    statuses.subscribeOnError(showMessage)

    val storiesWithTasks by viewModel.storiesWithTasks.collectAsState()
    storiesWithTasks.subscribeOnError(showMessage)

    val storylessTasks by viewModel.storylessTasks.collectAsState()
    storylessTasks.subscribeOnError(showMessage)

    val issues by viewModel.issues.collectAsState()
    issues.subscribeOnError(showMessage)

    val editResult by viewModel.editResult.collectAsState()
    editResult.subscribeOnError(showMessage)

    val deleteResult by viewModel.deleteResult.collectAsState()
    deleteResult.subscribeOnError(showMessage)
    deleteResult.takeIf { it is SuccessResult }?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    SprintScreenContent(
        sprint = sprint.data,
        isLoading = sprint is LoadingResult,
        isEditLoading = editResult is LoadingResult,
        isDeleteLoading = deleteResult is LoadingResult,
        statuses = statuses.data.orEmpty(),
        storiesWithTasks = storiesWithTasks.data.orEmpty(),
        storylessTasks = storylessTasks.data.orEmpty(),
        issues = issues.data.orEmpty(),
        editSprint = viewModel::editSprint,
        deleteSprint = viewModel::deleteSprint,
        navigateBack = navController::popBackStack,
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { type, parentId -> navController.navigateToCreateTaskScreen(type, parentId, sprintId) }
    )
}


@Composable
fun SprintScreenContent(
    sprint: Sprint?,
    isLoading: Boolean = false,
    isEditLoading: Boolean = false,
    isDeleteLoading: Boolean = false,
    statuses: List<Status> = emptyList(),
    storiesWithTasks: Map<CommonTask, List<CommonTask>> = emptyMap(),
    storylessTasks: List<CommonTask> = emptyList(),
    issues: List<CommonTask> = emptyList(),
    editSprint: (name: String, start: LocalDate, end: LocalDate) -> Unit = { _, _, _ -> },
    deleteSprint: () -> Unit = {},
    navigateBack: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: (type: CommonTaskType, parentId: Long?) -> Unit = { _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val dateFormatter = remember { LocalDate.Formats.ISO }


    var isMenuExpanded by remember { mutableStateOf(false) }
    AppBarWithBackButton(
        title = {
            Column {
                Text(
                    text = sprint?.name.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(Res.string.sprint_dates_template,
                        sprint?.start?.format(dateFormatter).orEmpty(),
                        sprint?.end?.format(dateFormatter).orEmpty()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_options),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // delete alert dialog
                var isDeleteAlertVisible by remember { mutableStateOf(false) }
                if (isDeleteAlertVisible) {
                    ConfirmActionDialog(
                        title = stringResource(Res.string.delete_sprint_title),
                        text = stringResource(Res.string.delete_sprint_text),
                        onConfirm = {
                            isDeleteAlertVisible = false
                            deleteSprint()
                        },
                        onDismiss = { isDeleteAlertVisible = false },
                        iconId = Res.drawable.ic_delete
                    )
                }

                var isEditDialogVisible by remember { mutableStateOf(false) }
                if (isEditDialogVisible) {
                    EditSprintDialog(
                        initialName = sprint?.name.orEmpty(),
                        initialStart = sprint?.start,
                        initialEnd = sprint?.end,
                        onConfirm = { name, start, end ->
                            editSprint(name, start, end)
                            isEditDialogVisible = false
                        },
                        onDismiss = { isEditDialogVisible = false }
                    )
                }

                DropdownMenu(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
                    ),
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    // edit
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            isEditDialogVisible = true
                        },
                        text = {
                            Text(
                                text = stringResource(Res.string.edit),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )

                    // delete
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            isDeleteAlertVisible = true
                        },
                        text = {
                            Text(
                                text = stringResource(Res.string.delete),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                }
            }
        },
        navigateBack = navigateBack
    )

    when {
        isLoading || sprint == null -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }

        isEditLoading || isDeleteLoading -> LoadingDialog()

        else -> SprintKanban(
            statuses = statuses,
            storiesWithTasks = storiesWithTasks,
            storylessTasks = storylessTasks,
            issues = issues,
            navigateToTask = navigateToTask,
            navigateToCreateTask = navigateToCreateTask
        )
    }
}


@Preview
@Composable
fun SprintScreenPreview() = TaigaMobileTheme {
    SprintScreenContent(
        sprint = Sprint(
            id = 0L,
            name = "0 sprint",
            start = LocalDate.now(),
            end = LocalDate.now(),
            order = 0,
            storiesCount = 0,
            isClosed = false
        )
    )
}
