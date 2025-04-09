package de.libf.taigamp.ui.screens.settings


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import de.libf.taigamp.state.ThemeSetting
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.components.DropdownSelector
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.screens.main.Routes
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.clickableUnindicated
import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.appearance
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.github_url
import taigamultiplatform.composeapp.generated.resources.ic_logout
import taigamultiplatform.composeapp.generated.resources.logout_text
import taigamultiplatform.composeapp.generated.resources.logout_title
import taigamultiplatform.composeapp.generated.resources.settings
import taigamultiplatform.composeapp.generated.resources.theme_dark
import taigamultiplatform.composeapp.generated.resources.theme_light
import taigamultiplatform.composeapp.generated.resources.theme_system
import taigamultiplatform.composeapp.generated.resources.theme_title
import taigamultiplatform.composeapp.generated.resources.username_template
import de.libf.taigamp.openUrl
import de.libf.taigamp.sendBugReport
import de.libf.taigamp.ui.utils.navigationBarsHeight
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.app_name
import taigamultiplatform.composeapp.generated.resources.app_name_with_version_template
import taigamultiplatform.composeapp.generated.resources.credits_message
import taigamultiplatform.composeapp.generated.resources.help
import taigamultiplatform.composeapp.generated.resources.source_code
import taigamultiplatform.composeapp.generated.resources.submit_report

@Composable
fun SettingsScreen(
    navController: NavController,
    showMessage: (StringResource) -> Unit = {}
) {
    val viewModel: SettingsViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val serverUrl by viewModel.serverUrl.collectAsState("")

    val user by viewModel.user.collectAsState()
    user.subscribeOnError(showMessage)

    val themeSetting by viewModel.themeSetting.collectAsState(ThemeSetting.System)

    SettingsScreenContent(
        avatarUrl = user.data?.avatarUrl,
        displayName = user.data?.displayName.orEmpty(),
        username = user.data?.username.orEmpty(),
        serverUrl = serverUrl,
        navigateBack = navController::popBackStack,
        logout = {
            viewModel.logout()
            navController.navigate(Routes.login) {
                popUpTo(Routes.settings) { inclusive = true }
            }
        },
        themeSetting = themeSetting,
        switchTheme = viewModel::switchTheme
    )
}

@Composable
fun SettingsScreenContent(
    avatarUrl: String?,
    displayName: String,
    username: String,
    serverUrl: String,
    navigateBack: () -> Unit = {},
    logout: () -> Unit = {},
    themeSetting: ThemeSetting = ThemeSetting.System,
    switchTheme: (ThemeSetting) -> Unit = {}
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize()
) {
    val (topBar, avatar, logoutIcon, userInfo, settings, appVersion) = createRefs()

    AppBarWithBackButton(
        title = { Text(stringResource(Res.string.settings)) },
        navigateBack = navigateBack,
        modifier = Modifier.constrainAs(topBar) {
            top.linkTo(parent.top)
        }
    )

    Image(
        painter = rememberAsyncImagePainter(
            model = avatarUrl ?: Res.drawable.default_avatar,
//            builder = {
//                error(Res.drawable.default_avatar)
//                crossfade(true)
//            },
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.large)
            .constrainAs(avatar) {
                top.linkTo(topBar.bottom, margin = 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )

    // logout
    var isAlertVisible by remember { mutableStateOf(false) }
    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(Res.string.logout_title),
            text = stringResource(Res.string.logout_text),
            onConfirm = {
                isAlertVisible = false
                logout()
            },
            onDismiss = { isAlertVisible = false },
            iconId = Res.drawable.ic_logout
        )
    }

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier.constrainAs(logoutIcon) {
            top.linkTo(avatar.top)
            start.linkTo(avatar.end)
        }
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_logout),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.constrainAs(userInfo) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(avatar.bottom, 8.dp)
        }
    ) {
        Text(
            text = displayName,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(Res.string.username_template, username),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = serverUrl,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }

    // settings itself
    Column (
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.constrainAs(settings) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(userInfo.bottom, 24.dp)
            }
    ) {
        // appearance
        SettingsBlock(
            titleId = Res.string.appearance,
            items = listOf {
                SettingItem(
                    textId = Res.string.theme_title,
                    itemWeight = 0.4f
                ) {
                    @Composable
                    fun titleForThemeSetting(themeSetting: ThemeSetting) = stringResource(
                        when (themeSetting) {
                            ThemeSetting.System -> Res.string.theme_system
                            ThemeSetting.Light -> Res.string.theme_light
                            ThemeSetting.Dark -> Res.string.theme_dark
                        }
                    )

                    DropdownSelector(
                        items = ThemeSetting.values().toList(),
                        selectedItem = themeSetting,
                        onItemSelected = { switchTheme(it) },
                        itemContent = {
                            Text(
                                text = titleForThemeSetting(it),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        selectedItemContent = {
                            Text(
                                text = titleForThemeSetting(it),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        )

        // help
        SettingsBlock(
            titleId = Res.string.help,
            items = listOf {
                SettingItem(
                    textId = Res.string.submit_report,
                    onClick = {
                        sendBugReport()
                    }
                )
            }
        )

    }


    Column(
        modifier = Modifier.constrainAs(appVersion) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom, 16.dp)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.credits_message),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(Res.string.app_name_with_version_template,
                stringResource(Res.string.app_name),
            ),
            style = MaterialTheme.typography.bodyLarge.merge(TextStyle(fontSize = 18.sp)),
            color = MaterialTheme.colorScheme.outline,
        )

        val githubUrl = stringResource(Res.string.github_url)
        Text(
            text = stringResource(Res.string.source_code),
            style = MaterialTheme.typography.bodyLarge.merge(SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)),
            modifier = Modifier.clickableUnindicated { openUrl(githubUrl) }
        )

        Spacer(Modifier.navigationBarsHeight(0.dp))
    }
}

@Composable
private fun SettingsBlock(
    titleId: StringResource,
    items: List<@Composable () -> Unit>
) {
    val verticalPadding = 2.dp

    Text(
        text = stringResource(titleId),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding)
    )

    Spacer(Modifier.height(verticalPadding))

    items.forEach { it() }

    Spacer(Modifier.height(verticalPadding * 4))
}

@Composable
private fun SettingItem(
    textId: StringResource,
    itemWeight: Float = 0.2f,
    onClick: () -> Unit = {},
    item: @Composable BoxScope.() -> Unit = {}
) = ContainerBox(
    verticalPadding = 10.dp,
    onClick = onClick
) {
//    assert(itemWeight > 0 && itemWeight < 1) { Timber.e("Item weight must be between 0 and 1") }
    if(itemWeight <= 0 || itemWeight >= 1) throw IllegalArgumentException("Item weight must be between 0 and 1")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(textId),
            modifier = Modifier.weight(1 - itemWeight, fill = false)
        )

        Box(
            modifier = Modifier.weight(itemWeight),
            contentAlignment = Alignment.CenterEnd,
            content = item
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() = TaigaMobileTheme {
    SettingsScreenContent(
        avatarUrl = null,
        displayName = "Cool Name",
        username = "username",
        serverUrl = "https://sample.server/"
    )
}

