package de.libf.taigamp.ui.screens.wiki.createpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.libf.taigamp.ui.components.dialogs.LoadingDialog
import de.libf.taigamp.ui.components.editors.Editor
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.utils.SuccessResult
import de.libf.taigamp.ui.utils.navigateToWikiPageScreen
import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.create_new_page

@Composable
fun WikiCreatePageScreen(
    navController: NavController,
    showMessage: (StringResource) -> Unit = {},
) {
    val viewModel: WikiCreatePageViewModel = viewModel()

    val creationResult by viewModel.creationResult.collectAsState()
    creationResult.subscribeOnError(showMessage)

    creationResult.takeIf { it is SuccessResult }?.data?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
            navController.navigateToWikiPageScreen(it.slug)
        }
    }

    WikiCreatePageScreenContent(
        isLoading = creationResult is LoadingResult,
        createWikiPage = viewModel::createWikiPage,
        navigateBack = navController::popBackStack
    )
}

@Composable
fun WikiCreatePageScreenContent(
    isLoading: Boolean = false,
    createWikiPage: (title: String, description: String) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {}
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    Editor(
        toolbarText = stringResource(Res.string.create_new_page),
        onSaveClick = createWikiPage,
        navigateBack = navigateBack
    )

    if (isLoading) {
        LoadingDialog()
    }
}

@Preview
@Composable
fun WikiCreatePageScreenPreview() {
    WikiCreatePageScreenContent()
}
