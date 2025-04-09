package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.CommonTaskExtended
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.entities.EpicShortInfo
import de.libf.taigamp.domain.entities.UserStoryShortInfo
import de.libf.taigamp.ui.components.dialogs.ConfirmActionDialog
import de.libf.taigamp.ui.components.buttons.AddButton
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.components.texts.CommonTaskTitle
import de.libf.taigamp.ui.screens.commontask.EditActions
import de.libf.taigamp.ui.screens.commontask.NavigationActions
import de.libf.taigamp.ui.utils.clickableUnindicated
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.ic_remove
import taigamultiplatform.composeapp.generated.resources.link_to_epic
import taigamultiplatform.composeapp.generated.resources.unlink_epic_text
import taigamultiplatform.composeapp.generated.resources.unlink_epic_title

@Suppress("FunctionName")
fun LazyListScope.CommonTaskBelongsTo(
    commonTask: CommonTaskExtended,
    navigationActions: NavigationActions,
    editActions: EditActions,
    showEpicsSelector: () -> Unit
) {
    // belongs to (epics)
    if (commonTask.taskType == CommonTaskType.UserStory) {
        items(commonTask.epicsShortInfo) {
            EpicItemWithAction(
                epic = it,
                onClick = { navigationActions.navigateToTask(it.id, CommonTaskType.Epic, it.ref) },
                onRemoveClick = { editActions.editEpics.remove(it) }
            )

            Spacer(Modifier.height(2.dp))
        }

        item {
            if (editActions.editEpics.isLoading) {
                DotsLoader()
            }

            AddButton(
                text = stringResource(Res.string.link_to_epic),
                onClick = { showEpicsSelector() }
            )
        }
    }

    // belongs to (story)
    if (commonTask.taskType == CommonTaskType.Task) {
        commonTask.userStoryShortInfo?.let {
            item {
                UserStoryItem(
                    story = it,
                    onClick = {
                        navigationActions.navigateToTask(it.id, CommonTaskType.UserStory, it.ref)
                    }
                )
            }
        }
    }
}

@Composable
private fun EpicItemWithAction(
    epic: EpicShortInfo,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(Res.string.unlink_epic_title),
            text = stringResource(Res.string.unlink_epic_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false },
            iconId = Res.drawable.ic_remove
        )
    }

    CommonTaskTitle(
        ref = epic.ref,
        title = epic.title,
        textColor = MaterialTheme.colorScheme.primary,
        indicatorColorsHex = listOf(epic.color),
        modifier = Modifier
            .weight(1f)
            .padding(end = 4.dp)
            .clickableUnindicated(onClick = onClick),
    )

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_remove),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun UserStoryItem(
    story: UserStoryShortInfo,
    onClick: () -> Unit
) = CommonTaskTitle(
    ref = story.ref,
    title = story.title,
    textColor = MaterialTheme.colorScheme.primary,
    indicatorColorsHex = story.epicColors,
    modifier = Modifier.clickableUnindicated(onClick = onClick)
)
