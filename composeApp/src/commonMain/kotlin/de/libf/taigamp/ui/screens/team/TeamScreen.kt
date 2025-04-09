package de.libf.taigamp.ui.screens.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import de.libf.taigamp.domain.entities.TeamMember
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.components.appbars.ClickableAppBar
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.components.texts.NothingToSeeHereText
import de.libf.taigamp.ui.screens.main.Routes
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.navigateToProfileScreen
import de.libf.taigamp.ui.utils.navigationBarsHeight
import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.power

@Composable
fun TeamScreen(
    navController: NavController,
    showMessage: (StringResource) -> Unit = {},
) {
    val viewModel: TeamViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState("")

    val team by viewModel.team.collectAsState()
    team.subscribeOnError(showMessage)

    TeamScreenContent(
        projectName = projectName,
        team = team.data.orEmpty(),
        isLoading = team is LoadingResult,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateBack = navController::popBackStack,
        onUserItemClick = { userId ->
            navController.navigateToProfileScreen(userId)
        }
    )
}

@Composable
fun TeamScreenContent(
    projectName: String,
    team: List<TeamMember> = emptyList(),
    isLoading: Boolean = false,
    onTitleClick: () -> Unit = {},
    navigateBack: () -> Unit = {},
    onUserItemClick: (userId: Long) -> Unit = { _ -> }
) = Column(Modifier.fillMaxSize()) {
    ClickableAppBar(
        projectName = projectName,
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        team.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NothingToSeeHereText()
            }
        }
        else -> {
            LazyColumn(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                items(team) { member ->
                    TeamMemberItem(
                        teamMember = member,
                        onUserItemClick = { onUserItemClick(member.id) }
                    )
                    Spacer(Modifier.height(6.dp))
                }

                item {
                    Spacer(Modifier.navigationBarsHeight(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamMemberItem(
    teamMember: TeamMember,
    onUserItemClick: () -> Unit
) = Row(
    modifier = Modifier.clickable { onUserItemClick() },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.6f)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = teamMember.avatarUrl ?: Res.drawable.default_avatar,
//                builder = {
//                    error(R.drawable.default_avatar)
//                    crossfade(true)
//                },
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text = teamMember.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = teamMember.role,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.weight(0.4f)
    ) {
        Text(
            text = teamMember.totalPower.toString(),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = stringResource(Res.string.power),
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Preview
@Composable
fun TeamScreenPreview() = TaigaMobileTheme {
    TeamScreenContent(
        projectName = "Name",
        team = List(3) {
            TeamMember(
                id = 0L,
                avatarUrl = null,
                name = "First Last",
                role = "Cool guy",
                username = "username",
                totalPower = 14
            )
        }
    )
}