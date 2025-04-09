package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.components.lists.UserItem
import de.libf.taigamp.ui.components.editors.SelectorList
import de.libf.taigamp.ui.components.texts.CommonTaskTitle
import de.libf.taigamp.ui.screens.commontask.CommonTaskViewModel
import de.libf.taigamp.ui.screens.commontask.EditAction
import de.libf.taigamp.ui.screens.commontask.SimpleEditAction
import de.libf.taigamp.ui.utils.toColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.choose_priority
import taigamultiplatform.composeapp.generated.resources.choose_severity
import taigamultiplatform.composeapp.generated.resources.choose_sprint
import taigamultiplatform.composeapp.generated.resources.choose_status
import taigamultiplatform.composeapp.generated.resources.choose_swimlane
import taigamultiplatform.composeapp.generated.resources.choose_type
import taigamultiplatform.composeapp.generated.resources.closed_sprint_name_template
import taigamultiplatform.composeapp.generated.resources.move_to_backlog
import taigamultiplatform.composeapp.generated.resources.search_epics
import taigamultiplatform.composeapp.generated.resources.search_members
import taigamultiplatform.composeapp.generated.resources.sprint_dates_template
import taigamultiplatform.composeapp.generated.resources.unclassifed

/**
 * Bunch of common selectors
 */
@Composable
fun Selectors(
    statusEntry: SelectorEntry<Status> = SelectorEntry(),
    typeEntry: SelectorEntry<Status> = SelectorEntry(),
    severityEntry: SelectorEntry<Status> = SelectorEntry(),
    priorityEntry: SelectorEntry<Status> = SelectorEntry(),
    sprintEntry: SelectorEntry<Sprint> = SelectorEntry(),
    epicsEntry: SelectorEntry<CommonTask> = SelectorEntry(),
    assigneesEntry: SelectorEntry<User> = SelectorEntry(),
    watchersEntry: SelectorEntry<User> = SelectorEntry(),
    swimlaneEntry: SelectorEntry<Swimlane> = SelectorEntry(),
) {
    // status editor
    SelectorList(
        titleHintId = Res.string.choose_status,
        items = statusEntry.edit.items,
        isVisible = statusEntry.isVisible,
        isSearchable = false,
        searchData = statusEntry.edit.searchItems,
        navigateBack = statusEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                statusEntry.edit.select(it)
                statusEntry.hide()
            }
        )
    }

    // type editor
    SelectorList(
        titleHintId = Res.string.choose_type,
        items = typeEntry.edit.items,
        isVisible = typeEntry.isVisible,
        isSearchable = false,
        searchData = typeEntry.edit.searchItems,
        navigateBack = typeEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                typeEntry.edit.select(it)
                typeEntry.hide()
            }
        )
    }
    
    // severity editor
    SelectorList(
        titleHintId = Res.string.choose_severity,
        items = severityEntry.edit.items,
        isVisible = severityEntry.isVisible,
        isSearchable = false,
        searchData = severityEntry.edit.searchItems,
        navigateBack = severityEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                severityEntry.edit.select(it)
                severityEntry.hide()
            }
        )
    }
    
    // priority editor
    SelectorList(
        titleHintId = Res.string.choose_priority,
        items = priorityEntry.edit.items,
        isVisible = priorityEntry.isVisible,
        isSearchable = false,
        searchData = priorityEntry.edit.searchItems,
        navigateBack = priorityEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                priorityEntry.edit.select(it)
                priorityEntry.hide()
            }
        )
    }

    // sprint editor
    SelectorList(
        titleHintId = Res.string.choose_sprint,
        itemsLazy = sprintEntry.edit.itemsLazy,
        isVisible = sprintEntry.isVisible,
        isSearchable = false,
        navigateBack = sprintEntry.hide
    ) {
        SprintItem(
            sprint = it,
            onClick = {
                sprintEntry.edit.select(it)
                sprintEntry.hide()
            }
        )
    }

    // epics editor
    SelectorList(
        titleHintId = Res.string.search_epics,
        itemsLazy = epicsEntry.edit.itemsLazy,
        isVisible = epicsEntry.isVisible,
        searchData = epicsEntry.edit.searchItems,
        navigateBack = epicsEntry.hide
    ) {
        EpicItem(
            epic = it,
            onClick = {
                epicsEntry.edit.select(it)
                epicsEntry.hide()
            }
        )
    }


    // assignees editor
    SelectorList(
        titleHintId = Res.string.search_members,
        items = assigneesEntry.edit.items,
        isVisible = assigneesEntry.isVisible,
        searchData = assigneesEntry.edit.searchItems,
        navigateBack = assigneesEntry.hide
    ) {
        MemberItem(
            member = it,
            onClick = {
                assigneesEntry.edit.select(it)
                assigneesEntry.hide()
            }
        )
    }

    // watchers editor
    SelectorList(
        titleHintId = Res.string.search_members,
        items = watchersEntry.edit.items,
        isVisible = watchersEntry.isVisible,
        searchData = watchersEntry.edit.searchItems,
        navigateBack = watchersEntry.hide
    ) {
        MemberItem(
            member = it,
            onClick = {
                watchersEntry.edit.select(it)
                watchersEntry.hide()
            }
        )
    }
    
    // swimlane editor
    SelectorList(
        titleHintId = Res.string.choose_swimlane,
        items = swimlaneEntry.edit.items,
        isVisible = swimlaneEntry.isVisible,
        isSearchable = false,
        navigateBack = swimlaneEntry.hide
    ) {
        SwimlaneItem(
            swimlane = it,
            onClick = {
                swimlaneEntry.edit.select(it)
                swimlaneEntry.hide()
            }
        )
    }
}

class SelectorEntry<TItem : Any> (
    val edit: EditAction<TItem, *> = SimpleEditAction(),
    val isVisible: Boolean = false,
    val hide: () -> Unit = {}
)

@Composable
private fun StatusItem(
    status: Status,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Text(
        text = status.name,
        color = status.color.toColor()
    )
}

@Composable
private fun SprintItem(
    sprint: Sprint?,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    val dateFormatter = remember { LocalDate.Formats.ISO }

    sprint.takeIf { it != CommonTaskViewModel.SPRINT_HEADER }?.also {
        Surface(
            contentColor = if (it.isClosed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
        ) {
            Column {
                Text(
                    if (it.isClosed) {
                        stringResource(Res.string.closed_sprint_name_template, it.name)
                    } else {
                        it.name
                    }
                )

                Text(
                    text = stringResource(Res.string.sprint_dates_template,
                        it.start.format(dateFormatter),
                        it.end.format(dateFormatter)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } ?: run {
        Text(
            text = stringResource(Res.string.move_to_backlog),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MemberItem(
    member: User,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    UserItem(member)
}

@Composable
private fun EpicItem(
    epic: CommonTask,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
   CommonTaskTitle(
       ref = epic.ref,
       title = epic.title,
       indicatorColorsHex = epic.colors,
       isInactive = epic.isClosed
   )
}

@Composable
private fun SwimlaneItem(
    swimlane: Swimlane,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    val swimlaneNullable = swimlane.takeIf { it != CommonTaskViewModel.SWIMLANE_HEADER }

    Text(
        text = swimlaneNullable?.name ?: stringResource(Res.string.unclassifed),
        style = MaterialTheme.typography.bodyLarge,
        color = swimlaneNullable?.let { MaterialTheme.colorScheme.onSurface } ?: MaterialTheme.colorScheme.primary
    )
}
