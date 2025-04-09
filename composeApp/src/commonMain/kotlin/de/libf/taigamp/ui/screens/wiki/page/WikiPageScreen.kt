package de.libf.taigamp.ui.screens.wiki.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavController
import de.libf.taigamp.domain.entities.Attachment
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.components.editors.Editor
import de.libf.taigamp.ui.components.lists.UserItem
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.screens.commontask.EditAction
import de.libf.taigamp.ui.components.lists.Attachments
import de.libf.taigamp.ui.components.lists.Description
import de.libf.taigamp.ui.theme.dialogTonalElevation
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.utils.SuccessResult
import de.libf.taigamp.ui.utils.navigateToProfileScreen
import de.libf.taigamp.ui.utils.now
import de.libf.taigamp.ui.utils.subscribeOnError
import io.ktor.utils.io.ByteReadChannel
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.delete
import taigamultiplatform.composeapp.generated.resources.delete_wiki_text
import taigamultiplatform.composeapp.generated.resources.delete_wiki_title
import taigamultiplatform.composeapp.generated.resources.edit
import taigamultiplatform.composeapp.generated.resources.ic_delete
import taigamultiplatform.composeapp.generated.resources.ic_options
import taigamultiplatform.composeapp.generated.resources.last_modification

@Composable
fun WikiPageScreen(
    slug: String,
    navController: NavController,
    showMessage: (StringResource) -> Unit = {},
) {
    val viewModel: WikiPageViewModel = koinViewModel()

    val page by viewModel.page.collectAsState()
    page.subscribeOnError(showMessage)

    val link by viewModel.link.collectAsState()
    link.subscribeOnError(showMessage)

    val editWikiPageResult by viewModel.editWikiPageResult.collectAsState()
    editWikiPageResult.subscribeOnError(showMessage)

    val deleteWikiPageResult by viewModel.deleteWikiPageResult.collectAsState()
    deleteWikiPageResult.subscribeOnError(showMessage)

    val attachments by viewModel.attachments.collectAsState()
    attachments.subscribeOnError(showMessage)

    val lastModifierUser by viewModel.lastModifierUser.collectAsState()

    val isLoading = page is LoadingResult || link is LoadingResult ||
        editWikiPageResult is LoadingResult || deleteWikiPageResult is LoadingResult ||
        attachments is LoadingResult

    deleteWikiPageResult.takeIf { it is SuccessResult }?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onOpen(slug)
    }

    WikiPageScreenContent(
        pageName = link.data?.title ?: slug,
        content = page.data?.content ?: "",
        lastModifierUser = lastModifierUser,
        lastModifierDate = page.data?.modifiedDate ?: LocalDateTime.now(),
        attachments = attachments.data.orEmpty(),
        isLoading = isLoading,
        navigateBack = navController::popBackStack,
        editWikiPage = viewModel::editWikiPage,
        deleteWikiPage = viewModel::deleteWikiPage,
        onUserItemClick = { userId ->
            navController.navigateToProfileScreen(userId)
        },
        editAttachments = EditAction(
            select = { (file, stream) -> viewModel.addPageAttachment(file, stream) },
            remove = viewModel::deletePageAttachment,
            isLoading = attachments is LoadingResult
        )
    )
}

@Composable
fun WikiPageScreenContent(
    pageName: String,
    content: String,
    lastModifierUser: User?,
    lastModifierDate: LocalDateTime,
    attachments: List<Attachment> = emptyList(),
    isLoading: Boolean = false,
    navigateBack: () -> Unit = {},
    editWikiPage: (content: String) -> Unit = { _ -> },
    deleteWikiPage: () -> Unit = {},
    onUserItemClick: (userId: Long) -> Unit = { _ -> },
    editAttachments: EditAction<Pair<String, ByteReadChannel>, Attachment> = EditAction(),
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    val sectionsPadding = 24.dp
    var isEditPageVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
    ) {

        WikiAppBar(
            pageName = pageName,
            navigateBack = navigateBack,
            editWikiPage = { isEditPageVisible = true },
            deleteWikiPage = deleteWikiPage,
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mainHorizontalScreenPadding)
        ) {

            // description
            Description(content)

            item {
                Spacer(
                    Modifier.height(sectionsPadding)
                )
            }

            // last modification
            item {
                Text(
                    text = stringResource(Res.string.last_modification),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                if (lastModifierUser != null) {
                    UserItem(
                        user = lastModifierUser,
                        dateTime = lastModifierDate,
                        onUserItemClick = {
                            onUserItemClick(lastModifierUser.id)
                        }
                    )
                }
            }

            item {
                Spacer(
                    Modifier.height(sectionsPadding)
                )
            }

            Attachments(
                attachments = attachments,
                editAttachments = editAttachments
            )

            item {
                Spacer(
                    Modifier.height(sectionsPadding)
                )
            }
        }

    }

    if (isEditPageVisible) {
        Editor(
            toolbarText = stringResource(Res.string.edit),
            title = pageName,
            description = content,
            showTitle = false,
            onSaveClick = { title, description ->
                editWikiPage(description)
                isEditPageVisible = false
            },
            navigateBack = {
                isEditPageVisible = false
            }
        )
    }
}

@Composable
fun WikiAppBar(
    pageName: String = "",
    editWikiPage: () -> Unit = {},
    deleteWikiPage: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    AppBarWithBackButton(
        title = {
            Text(
                text = pageName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_options),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            var isDeleteAlertVisible by remember { mutableStateOf(false) }
            if (isDeleteAlertVisible) {
                ConfirmActionDialog(
                    title = stringResource(Res.string.delete_wiki_title),
                    text = stringResource(Res.string.delete_wiki_text),
                    onConfirm = {
                        isDeleteAlertVisible = false
                        deleteWikiPage()
                    },
                    onDismiss = { isDeleteAlertVisible = false },
                    iconId = Res.drawable.ic_delete
                )
            }
            DropdownMenu(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
                ),
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {

                //Edit
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        editWikiPage()
                    },
                    text = {
                        Text(
                            text = stringResource(Res.string.edit),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )

                // Delete
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
        },
        navigateBack = navigateBack
    )
}

@Preview
@Composable
fun WikiPagePreview() {
    val creator = User(
        _id = 0,
        fullName = "Some cool fullname",
        photo = null,
        bigPhoto = null,
        username = "Some cool username"
    )

    WikiPageScreenContent(
        pageName = "Some page",
        content = "* Content *",
        lastModifierUser = creator,
        lastModifierDate = LocalDateTime.now()
    )
}