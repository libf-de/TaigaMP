package de.libf.taigamp.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.domain.entities.Stats
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.components.lists.ProjectCard
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.utils.ErrorResult
import de.libf.taigamp.ui.utils.LoadingResult

import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.closed_user_story
import taigamultiplatform.composeapp.generated.resources.contacts
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.profile
import taigamultiplatform.composeapp.generated.resources.projects
import taigamultiplatform.composeapp.generated.resources.username_template

@Composable
fun ProfileScreen(
    navController: NavController,
    showMessage: (StringResource) -> Unit = {},
    userId: Long
) {
    val viewModel: ProfileViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        viewModel.onOpen(userId)
    }

    val currentUser by viewModel.currentUser.collectAsState()
    currentUser.subscribeOnError(showMessage)
    val currentUserStats by viewModel.currentUserStats.collectAsState()
    currentUserStats.subscribeOnError(showMessage)
    val currentUserProjects by viewModel.currentUserProjects.collectAsState()
    currentUserProjects.subscribeOnError(showMessage)
    val currentProjectId by viewModel.currentProjectId.collectAsState(-1)

    ProfileScreenContent(
        navigateBack = navController::popBackStack,
        currentUser = currentUser.data,
        currentUserStats = currentUserStats.data,
        currentUserProjects = currentUserProjects.data ?: emptyList(),
        currentProjectId = currentProjectId,
        isLoading = currentUser is LoadingResult || currentUserStats is LoadingResult || currentUserProjects is LoadingResult,
        isError = currentUser is ErrorResult || currentUserStats is ErrorResult || currentUserProjects is ErrorResult
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProfileScreenContent(
    navigateBack: () -> Unit = {},
    currentUser: User? = null,
    currentUserStats: Stats? = null,
    currentUserProjects: List<Project> = emptyList(),
    currentProjectId: Long = 0,
    isLoading: Boolean = false,
    isError: Boolean = false
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    AppBarWithBackButton(
        title = { Text(stringResource(Res.string.profile)) },
        navigateBack = navigateBack
    )

    if (isLoading || isError) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularLoader()
        }
    } else {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(currentUser?.avatarUrl ?: Res.getUri("drawable/default_avatar.png"))
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(Res.drawable.default_avatar),
                    error = painterResource(Res.drawable.default_avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = currentUser?.fullName.orEmpty(),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = stringResource(Res.string.username_template, currentUser?.username.orEmpty()),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            currentUserStats?.roles?.let { roles ->
                items(roles) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ColumnTextData(currentUserStats?.totalNumProjects.toString(), stringResource(Res.string.projects))
                    ColumnTextData(
                        currentUserStats?.totalNumClosedUserStories.toString(),
                        stringResource(Res.string.closed_user_story)
                    )
                    ColumnTextData(currentUserStats?.totalNumContacts.toString(), stringResource(Res.string.contacts))
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.projects),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))
            }

            items(currentUserProjects) {
                ProjectCard(
                    project = it,
                    isCurrent = it.id == currentProjectId
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars).padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
private fun ColumnTextData(titleText: String, bodyText: String) = Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = titleText,
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(Modifier.height(2.dp))

    Text(
        text = bodyText,
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Preview
@Composable
fun ProfileScreenPreview() {
    val currentUser = User(
        _id = 123,
        fullName = null,
        photo = null,
        bigPhoto = null,
        username = "@username",
        name = "Cool user",
        pk = null
    )
    val currentUserStats = Stats(
        roles = listOf(
            "Design",
            "Front",
        ),
        totalNumClosedUserStories = 4,
        totalNumContacts = 48,
        totalNumProjects = 3
    )
    val currentUserProjects = listOf(
        Project(
            id = 1,
            name = "Cool project1",
            slug = "slug",
            description = "Cool description1",
            fansCount = 10,
            watchersCount = 3
        ),
        Project(
            id = 2,
            name = "Cool project2",
            slug = "slug",
            description = "Cool description2",
            fansCount = 1,
            watchersCount = 4
        ),
        Project(
            id = 3,
            name = "Cool project3",
            slug = "slug",
            description = "Cool description3",
            fansCount = 99,
            watchersCount = 0
        )
    )

    ProfileScreenContent(
        currentUser = currentUser,
        currentUserStats = currentUserStats,
        currentUserProjects = currentUserProjects
    )
}