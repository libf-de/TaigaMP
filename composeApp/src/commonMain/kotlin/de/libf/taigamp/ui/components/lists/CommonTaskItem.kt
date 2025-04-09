package de.libf.taigamp.ui.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.components.texts.CommonTaskTitle
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.NavigateToTask
import de.libf.taigamp.ui.utils.now
import de.libf.taigamp.ui.utils.toColor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.assignee_pattern
import taigamultiplatform.composeapp.generated.resources.epic
import taigamultiplatform.composeapp.generated.resources.issue
import taigamultiplatform.composeapp.generated.resources.task
import taigamultiplatform.composeapp.generated.resources.unassigned
import taigamultiplatform.composeapp.generated.resources.userstory

/**
 * Single task item
 */
@Composable
fun CommonTaskItem(
    commonTask: CommonTask,
    horizontalPadding: Dp = mainHorizontalScreenPadding,
    verticalPadding: Dp = 8.dp,
    showExtendedInfo: Boolean = false,
    navigateToTask: NavigateToTask = { _, _, _ -> }
) = ContainerBox(
    horizontalPadding, verticalPadding,
    onClick = { navigateToTask(commonTask.id, commonTask.taskType, commonTask.ref) }
) {
    val dateTimeFormatter = remember { LocalDateTime.Formats.ISO }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (showExtendedInfo) {
            Text(commonTask.projectInfo.name)

            Text(
                text = stringResource(
                    when (commonTask.taskType) {
                        CommonTaskType.UserStory -> Res.string.userstory
                        CommonTaskType.Task -> Res.string.task
                        CommonTaskType.Epic -> Res.string.epic
                        CommonTaskType.Issue -> Res.string.issue
                    }
                ).uppercase(),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = commonTask.status.name,
                color = commonTask.status.color.toColor(),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = commonTask.createdDate.format(dateTimeFormatter),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        CommonTaskTitle(
            ref = commonTask.ref,
            title = commonTask.title,
            indicatorColorsHex = commonTask.colors,
            isInactive = commonTask.isClosed,
            tags = commonTask.tags,
            isBlocked = commonTask.blockedNote != null
        )

        Text(
            text = commonTask.assignee?.fullName
                ?.let { stringResource(Res.string.assignee_pattern, it) }
                ?: stringResource(Res.string.unassigned),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
fun CommonTaskItemPreview() = TaigaMobileTheme {
    CommonTaskItem(
        CommonTask(
            id = 0L,
            createdDate = LocalDateTime.now(),
            title = "Very cool story",
            ref = 100,
            status = Status(
                id = 0L,
                name = "In progress",
                color = "#729fcf",
                type = StatusType.Status
            ),
            assignee = null,
            projectInfo = Project(0, "Name", "slug"),
            taskType = CommonTaskType.UserStory,
            isClosed = false,
            blockedNote = "Block reason"
        ),
        showExtendedInfo = true
    )
}
