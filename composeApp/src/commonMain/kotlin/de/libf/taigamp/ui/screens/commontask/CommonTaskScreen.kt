package de.libf.taigamp.ui.screens.commontask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.cash.paging.compose.collectAsLazyPagingItems
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.domain.entities.CustomField
import de.libf.taigamp.ui.components.editors.Editor
import de.libf.taigamp.ui.components.lists.SimpleTasksListWithTitle
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.components.dialogs.LoadingDialog
import de.libf.taigamp.ui.components.lists.Attachments
import de.libf.taigamp.ui.components.lists.Description
import de.libf.taigamp.ui.screens.commontask.components.*
import de.libf.taigamp.ui.screens.main.FilePicker
import de.libf.taigamp.ui.screens.main.LocalFilePicker
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import de.libf.taigamp.ui.utils.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.edit
import taigamultiplatform.composeapp.generated.resources.epic_slug
import taigamultiplatform.composeapp.generated.resources.issue_slug
import taigamultiplatform.composeapp.generated.resources.task_slug
import taigamultiplatform.composeapp.generated.resources.tasks
import taigamultiplatform.composeapp.generated.resources.userstories
import taigamultiplatform.composeapp.generated.resources.userstory_slug

@Composable
fun CommonTaskScreen(
    navController: NavController,
    commonTaskId: Long,
    commonTaskType: CommonTaskType,
    ref: Int,
    showMessage: (message: StringResource) -> Unit = {}
) {
    val viewModel: CommonTaskViewModel = koinViewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen(commonTaskId, commonTaskType)
    }

    val commonTask by viewModel.commonTask.collectAsState()
    commonTask.subscribeOnError(showMessage)

    val creator by viewModel.creator.collectAsState()
    creator.subscribeOnError(showMessage)

    val assignees by viewModel.assignees.collectAsState()
    assignees.subscribeOnError(showMessage)

    val watchers by viewModel.watchers.collectAsState()
    watchers.subscribeOnError(showMessage)

    val userStories by viewModel.userStories.collectAsState()
    userStories.subscribeOnError(showMessage)

    val tasks by viewModel.tasks.collectAsState()
    tasks.subscribeOnError(showMessage)

    val comments by viewModel.comments.collectAsState()
    comments.subscribeOnError(showMessage)

    val editBasicInfoResult by viewModel.editBasicInfoResult.collectAsState()
    editBasicInfoResult.subscribeOnError(showMessage)

    val statuses by viewModel.statuses.collectAsState()
    statuses.subscribeOnError(showMessage)
    val editStatusResult by viewModel.editStatusResult.collectAsState()
    editStatusResult.subscribeOnError(showMessage)

    val swimlanes by viewModel.swimlanes.collectAsState()
    swimlanes.subscribeOnError(showMessage)

    val sprints = viewModel.sprints.collectAsLazyPagingItems()
    sprints.subscribeOnError(showMessage)
    val editSprintResult by viewModel.editSprintResult.collectAsState()
    editSprintResult.subscribeOnError(showMessage)

    val epics = viewModel.epics.collectAsLazyPagingItems()
    epics.subscribeOnError(showMessage)
    val linkToEpicResult by viewModel.linkToEpicResult.collectAsState()
    linkToEpicResult.subscribeOnError(showMessage)

    val team by viewModel.team.collectAsState()
    team.subscribeOnError(showMessage)
    val teamSearched by viewModel.teamSearched.collectAsState()

    val customFields by viewModel.customFields.collectAsState()
    customFields.subscribeOnError(showMessage)

    val attachments by viewModel.attachments.collectAsState()
    attachments.subscribeOnError(showMessage)

    val tags by viewModel.tags.collectAsState()
    tags.subscribeOnError(showMessage)
    val tagsSearched by viewModel.tagsSearched.collectAsState()

    val editEpicColorResult by viewModel.editEpicColorResult.collectAsState()
    editEpicColorResult.subscribeOnError(showMessage)

    val editBlockedResult by viewModel.editBlockedResult.collectAsState()
    editBlockedResult.subscribeOnError(showMessage)

    val editDueDateResult by viewModel.editDueDateResult.collectAsState()
    editDueDateResult.subscribeOnError(showMessage)

    val deleteResult by viewModel.deleteResult.collectAsState()
    deleteResult.subscribeOnError(showMessage)
    deleteResult.takeIf { it is SuccessResult }?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    val promoteResult by viewModel.promoteResult.collectAsState()
    promoteResult.subscribeOnError(showMessage)
    promoteResult.takeIf { it is SuccessResult }?.data?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
            navController.navigateToTaskScreen(it.id, CommonTaskType.UserStory, it.ref)
        }
    }

    val projectName by viewModel.projectName.collectAsState("")
    val isAssignedToMe by viewModel.isAssignedToMe.collectAsState()
    val isWatchedByMe by viewModel.isWatchedByMe.collectAsState()

    fun createEditStatusAction(statusType: StatusType) = SimpleEditAction(
        items = statuses.data?.get(statusType).orEmpty(),
        select = viewModel::editStatus,
        isLoading = (editStatusResult as? LoadingResult)?.data == statusType
    )

    CommonTaskScreenContent(
        commonTaskType = commonTaskType,
        toolbarTitle = stringResource(
            when (commonTaskType) {
                CommonTaskType.UserStory -> Res.string.userstory_slug
                CommonTaskType.Task -> Res.string.task_slug
                CommonTaskType.Epic -> Res.string.epic_slug
                CommonTaskType.Issue -> Res.string.issue_slug
            }, ref
        ),
        toolbarSubtitle = projectName,
        commonTask = commonTask.data,
        creator = creator.data,
        isLoading = commonTask is LoadingResult,
        customFields = customFields.data?.fields.orEmpty(),
        attachments = attachments.data.orEmpty(),
        assignees = assignees.data.orEmpty(),
        watchers = watchers.data.orEmpty(),
        isAssignedToMe = isAssignedToMe,
        isWatchedByMe = isWatchedByMe,
        userStories = userStories.data.orEmpty(),
        tasks = tasks.data.orEmpty(),
        comments = comments.data.orEmpty(),
        editActions = EditActions(
            editStatus = createEditStatusAction(StatusType.Status),
            editType = createEditStatusAction(StatusType.Type),
            editSeverity = createEditStatusAction(StatusType.Severity),
            editPriority = createEditStatusAction(StatusType.Priority),
            editSwimlane = SimpleEditAction(
                items = swimlanes.data.orEmpty(),
                select = viewModel::editSwimlane,
                isLoading = swimlanes is LoadingResult
            ),
            editSprint = SimpleEditAction(
                itemsLazy = sprints,
                select = viewModel::editSprint,
                isLoading = editSprintResult is LoadingResult
            ),
            editEpics = EditAction(
                itemsLazy = epics,
                searchItems = viewModel::searchEpics,
                select = viewModel::linkToEpic,
                isLoading = linkToEpicResult is LoadingResult,
                remove = viewModel::unlinkFromEpic
            ),
            editAttachments = EditAction(
                select = { (file, stream) -> viewModel.addAttachment(file, stream) },
                remove = viewModel::deleteAttachment,
                isLoading = attachments is LoadingResult
            ),
            editAssignees = SimpleEditAction(
                items = teamSearched,
                searchItems = viewModel::searchTeam,
                select = { viewModel.addAssignee(it.id) },
                isLoading = assignees is LoadingResult,
                remove = { viewModel.removeAssignee(it.id) }
            ),
            editWatchers = SimpleEditAction(
                items = teamSearched,
                searchItems = viewModel::searchTeam,
                select = { viewModel.addWatcher(it.id) },
                isLoading = watchers is LoadingResult,
                remove = { viewModel.removeWatcher(it.id) }
            ),
            editComments = EditAction(
                select = viewModel::createComment,
                remove = viewModel::deleteComment,
                isLoading = comments is LoadingResult
            ),
            editBasicInfo = SimpleEditAction(
                select = { (title, description) -> viewModel.editBasicInfo(title, description) },
                isLoading = editBasicInfoResult is LoadingResult
            ),
            deleteTask = EmptyEditAction(
                select = { viewModel.deleteTask() },
                isLoading = deleteResult is LoadingResult
            ),
            promoteTask = EmptyEditAction(
                select = { viewModel.promoteToUserStory() },
                isLoading = promoteResult is LoadingResult
            ),
            editCustomField = SimpleEditAction(
                select = { (field, value) -> viewModel.editCustomField(field, value) },
                isLoading = customFields is LoadingResult
            ),
            editTags = EditAction(
                items = tagsSearched,
                searchItems = viewModel::searchTags,
                select = viewModel::addTag,
                remove = viewModel::deleteTag,
                isLoading = tags is LoadingResult
            ),
            editDueDate = EditAction(
                select = viewModel::editDueDate,
                remove = { viewModel.editDueDate(null) },
                isLoading = editDueDateResult is LoadingResult
            ),
            editEpicColor = SimpleEditAction(
                select = viewModel::editEpicColor,
                isLoading = editEpicColorResult is LoadingResult
            ),
            editAssign = EmptyEditAction(
                select =  { viewModel.addAssignee() },
                remove = { viewModel.removeAssignee() },
                isLoading = assignees is LoadingResult,
            ),
            editWatch = EmptyEditAction(
                select = { viewModel.addWatcher() },
                remove = { viewModel.removeWatcher() },
                isLoading = watchers is LoadingResult,
            ),
            editBlocked = EditAction(
                select = { viewModel.editBlocked(it) },
                remove = { viewModel.editBlocked(null) },
                isLoading = editBlockedResult is LoadingResult
            )
        ),
        navigationActions = NavigationActions(
            navigateBack = navController::popBackStack,
            navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Task, commonTaskId) },
            navigateToTask = navController::navigateToTaskScreen
        ),
        navigateToProfile = { userId ->
            navController.navigateToProfileScreen(userId)
        },
        showMessage = showMessage
    )
}

@Composable
fun CommonTaskScreenContent(
    commonTaskType: CommonTaskType,
    toolbarTitle: String,
    toolbarSubtitle: String,
    commonTask: CommonTaskExtended? = null,
    creator: User? = null,
    isLoading: Boolean = false,
    customFields: List<CustomField> = emptyList(),
    attachments: List<Attachment> = emptyList(),
    assignees: List<User> = emptyList(),
    watchers: List<User> = emptyList(),
    isAssignedToMe: Boolean = false,
    isWatchedByMe: Boolean = false,
    userStories: List<CommonTask> = emptyList(),
    tasks: List<CommonTask> = emptyList(),
    comments: List<Comment> = emptyList(),
    editActions: EditActions = EditActions(),
    navigationActions: NavigationActions = NavigationActions(),
    navigateToProfile: (userId: Long) -> Unit = {_ ->},
    showMessage: (message: StringResource) -> Unit = {}
) = Box(Modifier.fillMaxSize()) {
    var isTaskEditorVisible by remember { mutableStateOf(false) }

    var isStatusSelectorVisible by remember { mutableStateOf(false) }
    var isTypeSelectorVisible by remember { mutableStateOf(false) }
    var isSeveritySelectorVisible by remember { mutableStateOf(false) }
    var isPrioritySelectorVisible by remember { mutableStateOf(false) }
    var isSprintSelectorVisible by remember { mutableStateOf(false) }
    var isAssigneesSelectorVisible by remember { mutableStateOf(false) }
    var isWatchersSelectorVisible by remember { mutableStateOf(false) }
    var isEpicsSelectorVisible by remember { mutableStateOf(false) }
    var isSwimlaneSelectorVisible by remember { mutableStateOf(false) }

    var customFieldsValues by remember { mutableStateOf(emptyMap<Long, CustomFieldValue?>()) }
    customFieldsValues =
        customFields.associate { it.id to (if (it.id in customFieldsValues) customFieldsValues[it.id] else it.value) }

    Column(Modifier.fillMaxSize()) {
        CommonTaskAppBar(
            toolbarTitle = toolbarTitle,
            toolbarSubtitle = toolbarSubtitle,
            commonTaskType = commonTaskType,
            isBlocked = commonTask?.blockedNote != null,
            editActions = editActions,
            navigationActions = navigationActions,
            url = commonTask?.url ?: "",
            showTaskEditor = { isTaskEditorVisible = true },
            showMessage = showMessage
        )

        if (isLoading || creator == null || commonTask == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularLoader()
            }
        } else {
            val sectionsPadding = 16.dp

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = mainHorizontalScreenPadding)
                ) {

                    item {
                        Spacer(Modifier.height(sectionsPadding / 2))
                    }

                    CommonTaskHeader(
                        commonTask = commonTask,
                        editActions = editActions,
                        showStatusSelector = { isStatusSelectorVisible = true },
                        showSprintSelector = { isSprintSelectorVisible = true },
                        showTypeSelector = { isTypeSelectorVisible = true },
                        showSeveritySelector = { isSeveritySelectorVisible = true },
                        showPrioritySelector = { isPrioritySelectorVisible = true },
                        showSwimlaneSelector = { isSwimlaneSelectorVisible = true }
                    )

                    CommonTaskBelongsTo(
                        commonTask = commonTask,
                        navigationActions = navigationActions,
                        editActions = editActions,
                        showEpicsSelector = { isEpicsSelectorVisible = true }
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    Description(commonTask.description)

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskTags(
                        commonTask = commonTask,
                        editActions = editActions
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    if (commonTaskType != CommonTaskType.Epic) {
                        CommonTaskDueDate(
                            commonTask = commonTask,
                            editActions = editActions
                        )

                        item {
                            Spacer(Modifier.height(sectionsPadding))
                        }
                    }

                    CommonTaskCreatedBy(
                        creator = creator,
                        commonTask = commonTask,
                        navigateToProfile = navigateToProfile
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskAssignees(
                        assignees = assignees,
                        isAssignedToMe = isAssignedToMe,
                        editActions = editActions,
                        showAssigneesSelector = { isAssigneesSelectorVisible = true },
                        navigateToProfile = navigateToProfile
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskWatchers(
                        watchers = watchers,
                        isWatchedByMe = isWatchedByMe,
                        editActions = editActions,
                        showWatchersSelector = { isWatchersSelectorVisible = true },
                        navigateToProfile = navigateToProfile
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding * 2))
                    }

                    if (customFields.isNotEmpty()) {
                        CommonTaskCustomFields(
                            customFields = customFields,
                            customFieldsValues = customFieldsValues,
                            onValueChange = { itemId, value -> customFieldsValues = customFieldsValues - itemId + Pair(itemId, value) },
                            editActions = editActions
                        )

                        item {
                            Spacer(Modifier.height(sectionsPadding * 3))
                        }
                    }

                    Attachments(
                        attachments = attachments,
                        editAttachments = editActions.editAttachments
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    // user stories
                    if (commonTaskType == CommonTaskType.Epic) {
                        SimpleTasksListWithTitle(
                            titleText = Res.string.userstories,
                            bottomPadding = sectionsPadding,
                            commonTasks = userStories,
                            navigateToTask = navigationActions.navigateToTask
                        )
                    }

                    // tasks
                    if (commonTaskType == CommonTaskType.UserStory) {
                        SimpleTasksListWithTitle(
                            titleText = Res.string.tasks,
                            bottomPadding = sectionsPadding,
                            commonTasks = tasks,
                            navigateToTask = navigationActions.navigateToTask,
                            navigateToCreateCommonTask = navigationActions.navigateToCreateTask
                        )
                    }

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskComments(
                        comments = comments,
                        editActions = editActions,
                        navigateToProfile = navigateToProfile
                    )

                    item {
                        Spacer(
                            Modifier
                                .imePadding()
//                                .navigationBarsWithImePadding()
                                .height(72.dp)
                        )
                    }
                }

                CreateCommentBar(editActions.editComments.select)
            }
        }
    }

    // Bunch of list selectors
    Selectors(
        statusEntry = SelectorEntry(
            edit = editActions.editStatus,
            isVisible = isStatusSelectorVisible,
            hide = { isStatusSelectorVisible = false }
        ),
        typeEntry = SelectorEntry(
            edit = editActions.editType,
            isVisible = isTypeSelectorVisible,
            hide = { isTypeSelectorVisible = false }
        ),
        severityEntry = SelectorEntry(
            edit = editActions.editSeverity,
            isVisible = isSeveritySelectorVisible,
            hide = { isSeveritySelectorVisible = false }
        ),
        priorityEntry = SelectorEntry(
            edit = editActions.editPriority,
            isVisible = isPrioritySelectorVisible,
            hide = { isPrioritySelectorVisible = false }
        ),
        sprintEntry = SelectorEntry(
            edit = editActions.editSprint,
            isVisible = isSprintSelectorVisible,
            hide = { isSprintSelectorVisible = false }
        ),
        epicsEntry = SelectorEntry(
            edit = editActions.editEpics,
            isVisible = isEpicsSelectorVisible,
            hide = { isEpicsSelectorVisible = false }
        ),
        assigneesEntry = SelectorEntry(
            edit = editActions.editAssignees,
            isVisible = isAssigneesSelectorVisible,
            hide = { isAssigneesSelectorVisible = false }
        ),
        watchersEntry = SelectorEntry(
            edit = editActions.editWatchers,
            isVisible = isWatchersSelectorVisible,
            hide = { isWatchersSelectorVisible = false }
        ),
        swimlaneEntry = SelectorEntry(
            edit = editActions.editSwimlane,
            isVisible = isSwimlaneSelectorVisible,
            hide = { isSwimlaneSelectorVisible = false }
        )
    )

    // Editor
    if (isTaskEditorVisible || editActions.editBasicInfo.isLoading) {
        Editor(
            toolbarText = stringResource(Res.string.edit),
            title = commonTask?.title.orEmpty(),
            description = commonTask?.description.orEmpty(),
            onSaveClick = { title, description ->
                isTaskEditorVisible = false
                editActions.editBasicInfo.select(Pair(title, description))
            },
            navigateBack = { isTaskEditorVisible = false }
        )
    }

    if (editActions.run { listOf(editBasicInfo, promoteTask, deleteTask, editBlocked) }.any { it.isLoading }) {
        LoadingDialog()
    }
}

@Preview
@Composable
fun CommonTaskScreenPreview() = TaigaMobileTheme {
    CompositionLocalProvider(
        LocalFilePicker provides object : FilePicker() {}
    ) {
        CommonTaskScreenContent(
            commonTaskType = CommonTaskType.UserStory,
            toolbarTitle = "Userstory #99",
            toolbarSubtitle =  "Project #228",
            commonTask = null, // TODO left it null for now since I do not really use this preview
            creator = User(
                _id = 0L,
                fullName = "Full Name",
                photo = null,
                bigPhoto = null,
                username = "username"
            ),
            assignees = List(1) {
                User(
                    _id = 0L,
                    fullName = "Full Name",
                    photo = null,
                    bigPhoto = null,
                    username = "username"
                )
            },
            watchers = List(2) {
                User(
                    _id = 0L,
                    fullName = "Full Name",
                    photo = null,
                    bigPhoto = null,
                    username = "username"
                )
            },
            tasks = List(1) {
                CommonTask(
                    id = it.toLong(),
                    createdDate = LocalDateTime.now(),
                    title = "Very cool story",
                    ref = 100,
                    status = Status(
                        id = 1,
                        name = "In progress",
                        color = "#729fcf",
                        type = StatusType.Status
                    ),
                    assignee = null,
                    projectInfo = Project(0, "", ""),
                    taskType = CommonTaskType.UserStory,
                    isClosed = false
                )
            },
            comments = List(1) {
                Comment(
                    id = "",
                    author = User(
                        _id = 0L,
                        fullName = "Full Name",
                        photo = null,
                        bigPhoto = null,
                        username = "username"
                    ),
                    text = "This is comment text",
                    postDateTime = LocalDateTime.now(),
                    deleteDate = null
                )
            }
        )
    }
}
