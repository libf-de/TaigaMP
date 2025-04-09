package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.ui.components.buttons.AddButton
import de.libf.taigamp.ui.components.buttons.TextButton
import de.libf.taigamp.ui.components.lists.UserItemWithAction
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.screens.commontask.EditActions
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.add_assignee
import taigamultiplatform.composeapp.generated.resources.assign_to_me
import taigamultiplatform.composeapp.generated.resources.assigned_to
import taigamultiplatform.composeapp.generated.resources.ic_assignee_to_me
import taigamultiplatform.composeapp.generated.resources.ic_unassigned
import taigamultiplatform.composeapp.generated.resources.unassign

@Suppress("FunctionName")
fun LazyListScope.CommonTaskAssignees(
    assignees: List<User>,
    isAssignedToMe: Boolean,
    editActions: EditActions,
    showAssigneesSelector: () -> Unit,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        // assigned to
        Text(
            text = stringResource(Res.string.assigned_to),
            style = MaterialTheme.typography.titleMedium
        )
    }

    itemsIndexed(assignees) { index, item ->
        UserItemWithAction(
            user = item,
            onRemoveClick = { editActions.editAssignees.remove(item) },
            onUserItemClick = { navigateToProfile(item.id) }
        )

        if (index < assignees.lastIndex) {
            Spacer(Modifier.height(6.dp))
        }
    }

    // add assignee & loader
    item {
        if (editActions.editAssignees.isLoading) {
            DotsLoader()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AddButton(
                text = stringResource(Res.string.add_assignee),
                onClick = { showAssigneesSelector() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            val (buttonText: StringResource, buttonIcon: DrawableResource) = if (isAssignedToMe) {
                Res.string.unassign to Res.drawable.ic_unassigned
            } else {
                Res.string.assign_to_me to Res.drawable.ic_assignee_to_me
            }

            TextButton(
                text = stringResource(buttonText),
                icon = buttonIcon,
                onClick = {
                    if (isAssignedToMe) {
                        editActions.editAssign.remove(Unit)
                    } else {
                        editActions.editAssign.select(Unit)
                    }
                }
            )
        }
    }
}

