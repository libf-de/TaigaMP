package de.libf.taigamp.ui.screens.kanban

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ripple
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.ui.components.DropdownSelector
import de.libf.taigamp.ui.components.buttons.PlusButton
import de.libf.taigamp.ui.components.texts.CommonTaskTitle
import de.libf.taigamp.ui.theme.*
import de.libf.taigamp.ui.utils.navigationBarsHeight
import de.libf.taigamp.ui.utils.now
import de.libf.taigamp.ui.utils.toColor
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.status_with_number_template
import taigamultiplatform.composeapp.generated.resources.swimlane_title
import taigamultiplatform.composeapp.generated.resources.unclassifed


@Composable
fun KanbanBoard(
    statuses: List<Status>,
    stories: List<CommonTaskExtended> = emptyList(),
    team: List<User> = emptyList(),
    swimlanes: List<Swimlane?>,
    selectSwimlane: (Swimlane?) -> Unit = {},
    selectedSwimlane: Swimlane? = null,
    navigateToStory: (id: Long, ref: Int) -> Unit = { _, _ -> },
    navigateToCreateTask: (statusId: Long, swimlaneId: Long?) -> Unit = { _, _ -> }
) {
    val cellOuterPadding = 8.dp
    val cellPadding = 8.dp
    val cellWidth = 280.dp
    val backgroundCellColor = MaterialTheme.colorScheme.surfaceColorAtElevation(kanbanBoardTonalElevation)

    swimlanes.takeIf { it.isNotEmpty() }?.let {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(cellOuterPadding)
        ) {
            Text(
                text = stringResource(Res.string.swimlane_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.width(8.dp))

            DropdownSelector(
                items = swimlanes,
                selectedItem = selectedSwimlane,
                onItemSelected = selectSwimlane,
                itemContent = {
                    Text(
                        text = it?.name ?: stringResource(Res.string.unclassifed),
                        style = MaterialTheme.typography.bodyLarge,
                        color = it?.let { MaterialTheme.colorScheme.onSurface } ?: MaterialTheme.colorScheme.primary
                    )
                },
                selectedItemContent = {
                    Text(
                        text = it?.name ?: stringResource(Res.string.unclassifed),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }

    val storiesToDisplay = stories.filter { it.swimlane?.id == selectedSwimlane?.id }

    Row(
        Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {

       Spacer(Modifier.width(cellPadding))

        statuses.forEach { status ->
            val statusStories = storiesToDisplay.filter { it.status == status }

            Column {
                Header(
                    text = status.name,
                    storiesCount = statusStories.size,
                    cellWidth = cellWidth,
                    cellOuterPadding = cellOuterPadding,
                    stripeColor = status.color.toColor(),
                    backgroundColor = backgroundCellColor,
                    onAddClick = { navigateToCreateTask(status.id, selectedSwimlane?.id) }
                )

                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .width(cellWidth)
                        .background(backgroundCellColor)
                        .padding(cellPadding)
                ) {
                    items(statusStories) {
                        StoryItem(
                            story = it,
                            assignees = it.assignedIds.mapNotNull { id -> team.find { it.id == id } },
                            onTaskClick = { navigateToStory(it.id, it.ref) }
                        )
                    }

                    item {
                        Spacer(Modifier.navigationBarsHeight(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    text: String,
    storiesCount: Int,
    cellWidth: Dp,
    cellOuterPadding: Dp,
    stripeColor: Color,
    backgroundColor: Color,
    onAddClick: () -> Unit
) = Row(
    modifier = Modifier
        .padding(end = cellOuterPadding, bottom = cellOuterPadding)
        .width(cellWidth)
        .background(
            color = backgroundColor,
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            )
        ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val textStyle = MaterialTheme.typography.titleMedium

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.8f, fill = false)
    ) {
        Spacer(
            Modifier
                .padding(start = 10.dp)
                .size(
                    width = 10.dp,
                    height = with(LocalDensity.current) { textStyle.fontSize.toDp() + 2.dp }
                )
                .background(stripeColor)
        )

        Text(
            text = stringResource(Res.string.status_with_number_template,
                text.uppercase(), storiesCount
            ),
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp)
        )
    }

    PlusButton(
        tint = MaterialTheme.colorScheme.outline,
        onClick = onAddClick,
        modifier = Modifier.weight(0.2f)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryItem(
    story: CommonTaskExtended,
    assignees: List<User>,
    onTaskClick: () -> Unit
) = Surface(
    modifier = Modifier.fillMaxWidth().padding(4.dp),
    shape = MaterialTheme.shapes.small,
    shadowElevation = cardShadowElevation
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clickable(
                onClick = onTaskClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(12.dp)
    ) {
        story.epicsShortInfo.forEach {
            val textStyle = MaterialTheme.typography.bodySmall
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    Modifier
                        .size(with(LocalDensity.current) { textStyle.fontSize.toDp() })
                        .background(it.color.toColor(), CircleShape)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = it.title,
                    style = textStyle
                )
            }

            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(4.dp))

        CommonTaskTitle(
            ref = story.ref,
            title = story.title,
            isInactive = story.isClosed,
            tags = story.tags,
            isBlocked = story.blockedNote != null
        )

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            assignees.forEach {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = it.avatarUrl ?: Res.drawable.default_avatar,
//                        onState = {
//                            error(Res.drawable.default_avatar)
//                            crossfade(true)
//                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(28.dp)
                        .clip(CircleShape)
                        .weight(0.2f, fill = false)
                )
            }
        }

    }
}

@Preview
@Composable
fun KanbanBoardPreview() = TaigaMobileTheme {
    KanbanBoard(
        swimlanes = listOf(
            Swimlane(0, "Name", 0),
            Swimlane(0, "Another name", 1)
        ),
        statuses = listOf(
            Status(
                id = 0,
                name = "New",
                color = "#70728F",
                type = StatusType.Status
            ),
            Status(
                id = 1,
                name = "In progress",
                color = "#E47C40",
                type = StatusType.Status
            ),
            Status(
                id = 1,
                name = "Done",
                color = "#A8E440",
                type = StatusType.Status
            ),
            Status(
                id = 1,
                name = "Archived",
                color = "#A9AABC",
                type = StatusType.Status
            ),
        ),
        stories = List(5) {
            CommonTaskExtended(
                id = 0,
                status = Status(
                    id = 1,
                    name = "In progress",
                    color = "#E47C40",
                    type = StatusType.Status
                ),
                createdDateTime = LocalDateTime.now(),
                sprint = null,
                assignedIds = List(10) { it.toLong() },
                watcherIds = emptyList(),
                creatorId = 0,
                ref = 1,
                title = "Sample title",
                isClosed = false,
                description = "",
                epicsShortInfo = List(3) { EpicShortInfo(0, "Some title", 1, "#A8E440") },
                projectSlug = "",
                userStoryShortInfo = null,
                version = 0,
                color = null,
                type = null,
                priority = null,
                severity = null,
                taskType = CommonTaskType.UserStory,
                swimlane = null,
                dueDate = null,
                dueDateStatus = DueDateStatus.NotSet,
                url = ""
            )
        },
        team = List(10) {
            User(
                _id = it.toLong(),
                fullName = "Name Name",
                photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                bigPhoto = null,
                username = "username"
            )
        }
    )
}
