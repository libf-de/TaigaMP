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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.libf.taigamp.R
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.components.texts.CommonTaskTitle
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.NavigateToTask
import de.libf.taigamp.ui.utils.toColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
    val dateTimeFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (showExtendedInfo) {
            Text(commonTask.projectInfo.name)

            Text(
                text = stringResource(
                    when (commonTask.taskType) {
                        CommonTaskType.UserStory -> R.string.userstory
                        CommonTaskType.Task -> R.string.task
                        CommonTaskType.Epic -> R.string.epic
                        CommonTaskType.Issue -> R.string.issue
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
            text = commonTask.assignee?.fullName?.let { stringResource(R.string.assignee_pattern)
                .format(it) } ?: stringResource(R.string.unassigned),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
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
