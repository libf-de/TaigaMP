package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.components.editors.TextFieldWithHint
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import org.jetbrains.compose.resources.painterResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.comment_hint
import taigamultiplatform.composeapp.generated.resources.ic_send

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateCommentBar(
    onButtonClick: (String) -> Unit
) = Surface(
    modifier = Modifier.fillMaxWidth(),
    tonalElevation = 8.dp,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var commentTextValue by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = mainHorizontalScreenPadding)
            .imePadding()
//            .navigationBarsWithImePadding(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.large
                )
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            TextFieldWithHint(
                hintId = Res.string.comment_hint,
                maxLines = 3,
                value = commentTextValue,
                onValueChange = { commentTextValue = it }
            )
        }

        CompositionLocalProvider(
//            LocalMinimumTouchTargetEnforcement provides false
            LocalMinimumInteractiveComponentSize provides 0.dp
        ) {
            IconButton(
                onClick = {
                    commentTextValue.text.trim().takeIf { it.isNotEmpty() }?.let {
                        onButtonClick(it)
                        commentTextValue = TextFieldValue()
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.size(36.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
