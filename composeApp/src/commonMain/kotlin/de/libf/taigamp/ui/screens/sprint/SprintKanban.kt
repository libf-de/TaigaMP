package de.libf.taigamp.ui.screens.sprint

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.ui.components.buttons.PlusButton
import de.libf.taigamp.ui.components.lists.CommonTaskItem
import de.libf.taigamp.ui.components.texts.CommonTaskTitle
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.cardShadowElevation
import de.libf.taigamp.ui.theme.kanbanBoardTonalElevation
import de.libf.taigamp.ui.utils.NavigateToTask
import de.libf.taigamp.ui.utils.clickableUnindicated
import de.libf.taigamp.ui.utils.navigationBarsHeight
import de.libf.taigamp.ui.utils.now
import de.libf.taigamp.ui.utils.toColor
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.assignee_pattern
import taigamultiplatform.composeapp.generated.resources.default_avatar
import taigamultiplatform.composeapp.generated.resources.sprint_issues
import taigamultiplatform.composeapp.generated.resources.tasks_without_story
import taigamultiplatform.composeapp.generated.resources.unassigned
import taigamultiplatform.composeapp.generated.resources.userstory

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SprintKanban(
    statuses: List<Status>,
    storiesWithTasks: Map<CommonTask, List<CommonTask>>,
    storylessTasks: List<CommonTask> = emptyList(),
    issues: List<CommonTask> = emptyList(),
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: (type: CommonTaskType, parentId: Long?) -> Unit = { _, _ -> }
) = Column(
    modifier = Modifier.horizontalScroll(rememberScrollState())
) {
    val cellOuterPadding = 8.dp
    val cellPadding = 8.dp
    val cellWidth = 280.dp
    val userStoryHeadingWidth = cellWidth - 20.dp
    val minCellHeight = 80.dp
    val backgroundCellColor = MaterialTheme.colorScheme.surfaceColorAtElevation(kanbanBoardTonalElevation)
    val screenWidth = LocalWindowInfo.current.containerSize.width.dp //TODO: Check whether this works
//    val screenWidth = LocalContext.current.resources.configuration.screenWidthDp.dp
    val totalWidth = cellWidth * statuses.size + userStoryHeadingWidth + cellPadding * statuses.size

    Row(Modifier.padding(start = cellPadding, top = cellPadding)) {
        Header(
            text = stringResource(Res.string.userstory),
            cellWidth = userStoryHeadingWidth,
            cellPadding = cellPadding,
            stripeColor = backgroundCellColor,
            backgroundColor = Color.Transparent
        )

        statuses.forEach {
            Header(
                text = it.name,
                cellWidth = cellWidth,
                cellPadding = cellPadding,
                stripeColor = it.color.toColor(),
                backgroundColor = backgroundCellColor
            )
        }
    }

    LazyColumn {
        // stories with tasks
        storiesWithTasks.forEach { (story, tasks) ->
            item {
                Row(
                    Modifier
                        .height(IntrinsicSize.Max)
                        .padding(start = cellPadding)
                ) {
                    UserStoryItem(
                        cellPadding = cellPadding,
                        cellWidth = userStoryHeadingWidth,
                        minCellHeight = minCellHeight,
                        userStory = story,
                        onAddClick = { navigateToCreateTask(CommonTaskType.Task, story.id) },
                        onUserStoryClick = { navigateToTask(story.id, story.taskType, story.ref) }
                    )

                    statuses.forEach { status ->
                        Cell(
                            cellWidth = cellWidth,
                            cellOuterPadding = cellOuterPadding,
                            cellPadding = cellPadding,
                            backgroundCellColor = backgroundCellColor
                        ) {
                            tasks.filter { it.status == status }.forEach {
                                TaskItem(
                                    task = it,
                                    onTaskClick = { navigateToTask(it.id, it.taskType, it.ref) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // storyless tasks
        item {
            Row(
                Modifier
                    .height(IntrinsicSize.Max)
                    .padding(start = cellPadding)
            ) {
                CategoryItem(
                    titleId = Res.string.tasks_without_story,
                    cellPadding = cellPadding,
                    cellWidth = userStoryHeadingWidth,
                    minCellHeight = minCellHeight,
                    onAddClick = { navigateToCreateTask(CommonTaskType.Task, null) },
                )

                statuses.forEach { status ->
                    Cell(
                        cellWidth = cellWidth,
                        cellOuterPadding = cellOuterPadding,
                        cellPadding = cellPadding,
                        backgroundCellColor = backgroundCellColor
                    ) {
                        storylessTasks.filter { it.status == status }.forEach {
                            TaskItem(
                                task = it,
                                onTaskClick = { navigateToTask(it.id, it.taskType, it.ref) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(
                Modifier.height(4.dp)
                    .padding(start = cellPadding)
                    .width(totalWidth)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
        }

        // issues
        item {
            IssueHeader(
                width = screenWidth,
                padding = cellPadding,
                backgroundColor = backgroundCellColor,
                onAddClick = { navigateToCreateTask(CommonTaskType.Issue, null) }
            )
        }

        items(issues) {
            Row(Modifier.width(totalWidth)) {
                Row(
                    Modifier.width(screenWidth)
                        .padding(vertical = 4.dp)
                        .background(backgroundCellColor)
                ) {
                    CommonTaskItem(
                        commonTask = it,
                        navigateToTask = navigateToTask
                    )
                }
            }
        }

        item {
            Spacer(Modifier.navigationBarsHeight(8.dp))
        }
    }
}

@Composable
private fun Header(
    text: String,
    cellWidth: Dp,
    cellPadding: Dp,
    stripeColor: Color,
    backgroundColor: Color,
) = Column(
    modifier = Modifier
        .padding(end = cellPadding, bottom = cellPadding)
        .width(cellWidth)
        .background(
            color = backgroundColor,
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            )
        ),
    horizontalAlignment = Alignment.Start
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(8.dp)
    )

    Spacer(
        Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(stripeColor)
    )
}

@Composable
private fun IssueHeader(
    width: Dp,
    padding: Dp,
    backgroundColor: Color,
    onAddClick: () -> Unit
) = Row(
    modifier = Modifier
        .width(width)
        .padding(padding)
        .clip(MaterialTheme.shapes.extraSmall)
        .background(backgroundColor)
        .padding(horizontal = 6.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text(
        text = stringResource(Res.string.sprint_issues).uppercase(),
        modifier = Modifier.weight(0.8f, fill = false)
    )

    PlusButton(
        tint = MaterialTheme.colorScheme.outline,
        onClick = onAddClick,
        modifier = Modifier.weight(0.2f)
    )
}

@Composable
private fun UserStoryItem(
    cellPadding: Dp,
    cellWidth: Dp,
    minCellHeight: Dp,
    userStory: CommonTask,
    onAddClick: () -> Unit,
    onUserStoryClick: () -> Unit
) = Row(
    modifier = Modifier
        .padding(end = cellPadding, bottom = cellPadding)
        .width(cellWidth)
        .heightIn(min = minCellHeight),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Column(
        modifier = Modifier.fillMaxWidth().weight(0.8f, fill = false)
    ) {
        CommonTaskTitle(
            ref = userStory.ref,
            title = userStory.title,
            indicatorColorsHex = userStory.colors,
            isInactive = userStory.isClosed,
            tags = userStory.tags,
            isBlocked = userStory.blockedNote != null,
            modifier = Modifier.padding(top = 4.dp)
                .clickableUnindicated(onClick = onUserStoryClick)
        )

        Text(
            text = userStory.status.name,
            color = userStory.status.color.toColor(),
            style = MaterialTheme.typography.bodyMedium
        )
    }

    PlusButton(
        tint = MaterialTheme.colorScheme.outline,
        onClick = onAddClick,
        modifier = Modifier.weight(0.2f)
    )
}

@Composable
private fun CategoryItem(
    titleId: StringResource,
    cellPadding: Dp,
    cellWidth: Dp,
    minCellHeight: Dp,
    onAddClick: () -> Unit,
) = Column(
    modifier = Modifier
        .padding(end = cellPadding, bottom = cellPadding)
        .width(cellWidth)
        .heightIn(min = minCellHeight)
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(titleId),
            modifier = Modifier
                .weight(0.8f, fill = false)
                .padding(top = 4.dp)
        )

        PlusButton(
            tint = MaterialTheme.colorScheme.outline,
            onClick = onAddClick,
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
private fun Cell(
    cellWidth: Dp,
    cellOuterPadding: Dp,
    cellPadding: Dp,
    backgroundCellColor: Color,
    content: @Composable ColumnScope.() -> Unit
) = Column(
    modifier = Modifier
        .fillMaxHeight()
        .padding(end = cellOuterPadding, bottom = cellOuterPadding)
        .width(cellWidth)
        .background(backgroundCellColor)
        .padding(cellPadding),
    content = content
)

@Composable
private fun TaskItem(
    task: CommonTask,
    onTaskClick: () -> Unit
) = Surface(
    modifier = Modifier.fillMaxWidth().padding(4.dp),
    shape = MaterialTheme.shapes.small,
    shadowElevation = cardShadowElevation
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .clickable(
                onClick = onTaskClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(12.dp)
    ) {
        Column(Modifier.weight(0.8f, fill = false)) {
            CommonTaskTitle(
                ref = task.ref,
                title = task.title,
                indicatorColorsHex = task.colors,
                isInactive = task.isClosed,
                tags = task.tags,
                isBlocked = task.blockedNote != null
            )

            Text(
                text = task.assignee?.fullName?.let {
                    stringResource(Res.string.assignee_pattern, it)
                } ?: stringResource(Res.string.unassigned),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        task.assignee?.let {
            Image(
                painter = rememberAsyncImagePainter(
                    model = it.avatarUrl ?: Res.drawable.default_avatar,
//                    builder = {
//                        error(R.drawable.default_avatar)
//                        crossfade(true)
//                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(32.dp)
                    .clip(CircleShape)
                    .weight(0.2f, fill = false)
            )
        }
    }
}

@Preview
@Composable
fun SprintKanbanPreview() = TaigaMobileTheme {
    SprintKanban(
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
        storiesWithTasks = List(5) {
            CommonTask(
                id = it.toLong(),
                createdDate = LocalDateTime.now(),
                title = "Very cool story",
                ref = 100,
                status = Status(
                    id = 1,
                    name = "In progress",
                    color = "#E47C40",
                    type = StatusType.Status
                ),
                assignee = User(
                    _id = it.toLong(),
                    fullName = "Name Name",
                    photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                    bigPhoto = null,
                    username = "username"
                ),
                projectInfo = Project(0, "", ""),
                taskType = CommonTaskType.UserStory,
                isClosed = false
            ) to listOf(
                CommonTask(
                    id = it.toLong(),
                    createdDate = LocalDateTime.now(),
                    title = "Very cool story Very cool story Very cool story",
                    ref = 100,
                    status = Status(
                        id = 1,
                        name = "In progress",
                        color = "#E47C40",
                        type = StatusType.Status
                    ),
                    assignee = User(
                        _id = it.toLong(),
                        fullName = "Name Name",
                        photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                        bigPhoto = null,
                        username = "username"
                    ),
                    projectInfo = Project(0, "", ""),
                    taskType = CommonTaskType.Task,
                    isClosed = false
                ),
                CommonTask(
                    id = it.toLong() + 2,
                    createdDate = LocalDateTime.now(),
                    title = "Very cool story",
                    ref = 100,
                    status = Status(
                        id = 1,
                        name = "In progress",
                        color = "#E47C40",
                        type = StatusType.Status
                    ),
                    assignee = User(
                        _id = it.toLong(),
                        fullName = "Name Name",
                        photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                        bigPhoto = null,
                        username = "username"
                    ),
                    projectInfo = Project(0, "", ""),
                    taskType = CommonTaskType.Task,
                    isClosed = false
                ),
                CommonTask(
                    id = it.toLong() + 2,
                    createdDate = LocalDateTime.now(),
                    title = "Very cool story",
                    ref = 100,
                    status = Status(
                        id = 0,
                        name = "New",
                        color = "#70728F",
                        type = StatusType.Status
                    ),
                    assignee = User(
                        _id = it.toLong(),
                        fullName = "Name Name",
                        photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                        bigPhoto = null,
                        username = "username"
                    ),
                    projectInfo = Project(0, "", ""),
                    taskType = CommonTaskType.Task,
                    isClosed = false
                )
            )
        }.toMap(),
        issues = List(10) {
            CommonTask(
                id = it.toLong() + 1,
                createdDate = LocalDateTime.now(),
                title = "Very cool story",
                ref = 100,
                status = Status(
                    id = 0,
                    name = "New",
                    color = "#70728F",
                    type = StatusType.Status
                ),
                assignee = User(
                    _id = it.toLong(),
                    fullName = "Name Name",
                    photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                    bigPhoto = null,
                    username = "username"
                ),
                projectInfo = Project(0, "", ""),
                taskType = CommonTaskType.Issue,
                isClosed = false
            )
        }
    )
}
