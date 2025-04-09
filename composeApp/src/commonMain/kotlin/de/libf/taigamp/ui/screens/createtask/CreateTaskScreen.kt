package de.libf.taigamp.ui.screens.createtask

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavController
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.utils.SuccessResult
import de.libf.taigamp.ui.components.editors.Editor
import de.libf.taigamp.ui.components.dialogs.LoadingDialog
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.utils.navigateToTaskScreen
import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.create_epic
import taigamultiplatform.composeapp.generated.resources.create_issue
import taigamultiplatform.composeapp.generated.resources.create_task
import taigamultiplatform.composeapp.generated.resources.create_userstory

@Composable
fun CreateTaskScreen(
    navController: NavController,
    commonTaskType: CommonTaskType,
    parentId: Long? = null,
    sprintId: Long? = null,
    statusId: Long? = null,
    swimlaneId: Long? = null,
    showMessage: (message: StringResource) -> Unit = {},
) {
    val viewModel: CreateTaskViewModel = koinViewModel()

    val creationResult by viewModel.creationResult.collectAsState()
    creationResult.subscribeOnError(showMessage)

    creationResult.takeIf { it is SuccessResult }?.data?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
            navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
        }
    }

    CreateTaskScreenContent(
        title = stringResource(
            when (commonTaskType) {
                CommonTaskType.UserStory -> Res.string.create_userstory
                CommonTaskType.Task -> Res.string.create_task
                CommonTaskType.Epic -> Res.string.create_epic
                CommonTaskType.Issue -> Res.string.create_issue
            }
        ),
        isLoading = creationResult is LoadingResult,
        createTask = { title, description -> viewModel.createTask(commonTaskType, title, description, parentId, sprintId, statusId, swimlaneId) },
        navigateBack = navController::popBackStack
    )
}

@Composable
fun CreateTaskScreenContent(
    title: String,
    isLoading: Boolean = false,
    createTask: (title: String, description: String) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {}
) = Box(Modifier.fillMaxSize()) {
    Editor(
        toolbarText = title,
        onSaveClick = createTask,
        navigateBack = navigateBack
    )

    if (isLoading) {
        LoadingDialog()
    }
}

@Preview
@Composable
fun CreateTaskScreenPreview() = TaigaMobileTheme {
    CreateTaskScreenContent(
        title = "Create task"
    )
}
