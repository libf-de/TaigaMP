package de.libf.taigamp.ui.components.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.no
import taigamultiplatform.composeapp.generated.resources.yes

/**
 * Standard confirmation alert with "yes" "no" buttons, title and text
 */
@Composable
fun ConfirmActionDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    iconId: DrawableResource? = null
) = AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(
                text = stringResource(Res.string.yes),
                style = MaterialTheme.typography.titleMedium
            )
        }
    },
    dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(
                text = stringResource(Res.string.no),
                style = MaterialTheme.typography.titleMedium
            )
        }
    },
    title = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
    },
    text = { Text(text) },
    icon = iconId?.let {
        {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(it),
                contentDescription = null
            )
        }
    }
)
