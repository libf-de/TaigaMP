package de.libf.taigamp.ui.components.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.Attachment
import de.libf.taigamp.openUrl
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.components.texts.SectionTitle
import de.libf.taigamp.ui.screens.commontask.EditAction
import de.libf.taigamp.ui.screens.main.LocalFilePicker
import io.ktor.utils.io.ByteReadChannel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.attachments_template
import taigamultiplatform.composeapp.generated.resources.ic_attachment
import taigamultiplatform.composeapp.generated.resources.ic_delete
import taigamultiplatform.composeapp.generated.resources.ic_remove
import taigamultiplatform.composeapp.generated.resources.remove_attachment_text
import taigamultiplatform.composeapp.generated.resources.remove_attachment_title

@Suppress("FunctionName")
fun LazyListScope.Attachments(
    attachments: List<Attachment>,
    editAttachments: EditAction<Pair<String, ByteReadChannel>, Attachment>
) {
    item {
        val filePicker = LocalFilePicker.current
        SectionTitle(
            text = stringResource(Res.string.attachments_template, attachments.size),
            onAddClick = {
                filePicker.requestFile { file, stream -> editAttachments.select(file to stream) }
            }
        )
    }

    items(attachments) {
        AttachmentItem(
            attachment = it,
            onRemoveClick = { editAttachments.remove(it) }
        )
    }

    item {
        if (editAttachments.isLoading) {
            DotsLoader()
        }
    }
}

@Composable
private fun AttachmentItem(
    attachment: Attachment,
    onRemoveClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(Res.string.remove_attachment_title),
            text = stringResource(Res.string.remove_attachment_text),
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
        modifier = Modifier
            .weight(1f, fill = false)
            .padding(end = 4.dp)
    ) {
//        val activity = LocalContext.current.activity
        Icon(
            painter = painterResource(Res.drawable.ic_attachment),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(end = 2.dp)
        )

        Text(
            text = attachment.name,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                openUrl(attachment.url)
            }
        )
    }

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_delete),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
    }

}
