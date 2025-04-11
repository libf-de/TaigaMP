package de.libf.taigamp.ui.components.lists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.utils.clickableUnindicated
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.ic_remove
import taigamultiplatform.composeapp.generated.resources.remove_user_text
import taigamultiplatform.composeapp.generated.resources.remove_user_title

/**
 * User info (name and avatar).
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun UserItem(
    user: User,
    dateTime: LocalDateTime? = null,
    onUserItemClick: () -> Unit = { }
) = Row(
    modifier = Modifier.clickableUnindicated { onUserItemClick() },
    verticalAlignment = Alignment.CenterVertically
) {
    val dateTimeFormatter = remember { LocalDateTime.Formats.ISO }
    val imageSize = if (dateTime != null) 46.dp else 40.dp

    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(user.avatarUrl ?: Res.getUri("drawable/default_avatar.png"))
            .crossfade(true)
            .build(),
        placeholder = painterResource(Res.drawable.default_avatar),
        error = painterResource(Res.drawable.default_avatar),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
    )

    Spacer(Modifier.width(6.dp))

    Column {
        Text(
            text = user.displayName,
            style = MaterialTheme.typography.titleMedium
        )

        dateTime?.let {
            Text(
                text = it.format(dateTimeFormatter),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun UserItemWithAction(
    user: User,
    onRemoveClick: () -> Unit,
    onUserItemClick: () -> Unit = { }
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(Res.string.remove_user_title),
            text = stringResource(Res.string.remove_user_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false },
            iconId = Res.drawable.ic_remove
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserItem(
            user = user,
            onUserItemClick = onUserItemClick
        )

        IconButton(onClick = { isAlertVisible = true }) {
            Icon(
                painter = painterResource(Res.drawable.ic_remove),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Composable
fun UserItemPreview() = TaigaMobileTheme {
    UserItem(
        user = User(
            _id = 0L,
            fullName = "Full Name",
            photo = null,
            bigPhoto = null,
            username = "username"
        )
    )
}