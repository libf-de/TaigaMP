package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.CommonTaskExtended
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.ui.components.badges.ClickableBadge
import de.libf.taigamp.ui.components.pickers.ColorPicker
import de.libf.taigamp.ui.screens.commontask.EditActions
import de.libf.taigamp.ui.theme.taigaRed
import de.libf.taigamp.ui.utils.toColor
import de.libf.taigamp.ui.utils.toHex
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.blocked
import taigamultiplatform.composeapp.generated.resources.ic_lock
import taigamultiplatform.composeapp.generated.resources.no_sprint
import taigamultiplatform.composeapp.generated.resources.unclassifed

@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionName")
fun LazyListScope.CommonTaskHeader(
    commonTask: CommonTaskExtended,
    editActions: EditActions,
    showStatusSelector: () -> Unit,
    showSprintSelector: () -> Unit,
    showTypeSelector: () -> Unit,
    showSeveritySelector: () -> Unit,
    showPrioritySelector: () -> Unit,
    showSwimlaneSelector: () -> Unit
) {
    val badgesPadding = 8.dp

    item {

        commonTask.blockedNote?.trim()?.let {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(taigaRed, MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                val space = 4.dp

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_lock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.width(space))

                    Text(stringResource(Res.string.blocked))
                }

                if (it.isNotEmpty()) {
                    Spacer(Modifier.width(space))

                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(badgesPadding))
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(badgesPadding),
            verticalArrangement = Arrangement.spacedBy(badgesPadding),
        ) {
            // epic color
            if (commonTask.taskType == CommonTaskType.Epic) {
                ColorPicker(
                    size = 32.dp,
                    color = commonTask.color.orEmpty().toColor(),
                    onColorPicked = { editActions.editEpicColor.select(it.toHex()) }
                )

            }

            // status
            ClickableBadge(
                text = commonTask.status.name,
                colorHex = commonTask.status.color,
                onClick = { showStatusSelector() },
                isLoading = editActions.editStatus.isLoading
            )

            // sprint
            if (commonTask.taskType != CommonTaskType.Epic) {
                ClickableBadge(
                    text = commonTask.sprint?.name ?: stringResource(Res.string.no_sprint),
                    color = commonTask.sprint?.let { MaterialTheme.colorScheme.primary }
                        ?: MaterialTheme.colorScheme.outline,
                    onClick = { showSprintSelector() },
                    isLoading = editActions.editSprint.isLoading,
                    isClickable = commonTask.taskType != CommonTaskType.Task
                )
            }

            // swimlane
            if (commonTask.taskType == CommonTaskType.UserStory) {
                ClickableBadge(
                    text = commonTask.swimlane?.name ?: stringResource(Res.string.unclassifed),
                    color = commonTask.swimlane?.let { MaterialTheme.colorScheme.primary }
                        ?: MaterialTheme.colorScheme.outline,
                    isLoading = editActions.editSwimlane.isLoading,
                    onClick = { showSwimlaneSelector() }
                )
            }

            if (commonTask.taskType == CommonTaskType.Issue) {
                // type
                ClickableBadge(
                    text = commonTask.type!!.name,
                    colorHex = commonTask.type.color,
                    onClick = { showTypeSelector() },
                    isLoading = editActions.editType.isLoading
                )

                // severity
                ClickableBadge(
                    text = commonTask.severity!!.name,
                    colorHex = commonTask.severity.color,
                    onClick = { showSeveritySelector() },
                    isLoading = editActions.editSeverity.isLoading
                )

                // priority
                ClickableBadge(
                    text = commonTask.priority!!.name,
                    colorHex = commonTask.priority.color,
                    onClick = { showPrioritySelector() },
                    isLoading = editActions.editPriority.isLoading
                )
            }
        }

        // title
        Text(
            text = commonTask.title,
            style = MaterialTheme.typography.headlineSmall.let {
                if (commonTask.isClosed) {
                    it.merge(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.outline,
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                } else {
                    it
                }
            }
        )

        Spacer(Modifier.height(4.dp))
    }
}
