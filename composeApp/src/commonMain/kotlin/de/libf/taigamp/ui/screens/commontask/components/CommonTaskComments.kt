package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.Comment
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.components.lists.UserItem
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.components.texts.MarkdownText
import de.libf.taigamp.ui.components.texts.SectionTitle
import de.libf.taigamp.ui.screens.commontask.EditActions
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.comments_template
import taigamultiplatform.composeapp.generated.resources.delete_comment_text
import taigamultiplatform.composeapp.generated.resources.delete_comment_title
import taigamultiplatform.composeapp.generated.resources.ic_delete

@Suppress("FunctionName")
fun LazyListScope.CommonTaskComments(
    comments: List<Comment>,
    editActions: EditActions,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        SectionTitle(stringResource(Res.string.comments_template, comments.size))
    }

    itemsIndexed(comments) { index, item ->
        CommentItem(
            comment = item,
            onDeleteClick = { editActions.editComments.remove(item) },
            navigateToProfile = navigateToProfile
        )

        if (index < comments.lastIndex) {
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    item {
        if (editActions.editComments.isLoading) {
            DotsLoader()
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    onDeleteClick: () -> Unit,
    navigateToProfile: (userId: Long) -> Unit
) = Column {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(Res.string.delete_comment_title),
            text = stringResource(Res.string.delete_comment_text),
            onConfirm = {
                isAlertVisible = false
                onDeleteClick()
            },
            onDismiss = { isAlertVisible = false },
            iconId = Res.drawable.ic_delete
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserItem(
            user = comment.author,
            dateTime = comment.postDateTime,
            onUserItemClick = { navigateToProfile(comment.author.id) }
        )

        if (comment.canDelete) {
            IconButton(onClick = { isAlertVisible = true }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    MarkdownText(
        text = comment.text,
        modifier = Modifier.padding(start = 4.dp)
    )
}
