package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import taigamultiplatform.composeapp.generated.resources.add_watcher
import taigamultiplatform.composeapp.generated.resources.ic_unwatch
import taigamultiplatform.composeapp.generated.resources.ic_watch
import taigamultiplatform.composeapp.generated.resources.unwatch
import taigamultiplatform.composeapp.generated.resources.watch
import taigamultiplatform.composeapp.generated.resources.watchers

@Suppress("FunctionName")
fun LazyListScope.CommonTaskWatchers(
    watchers: List<User>,
    isWatchedByMe: Boolean,
    editActions: EditActions,
    showWatchersSelector: () -> Unit,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        // watchers
        Text(
            text = stringResource(Res.string.watchers),
            style = MaterialTheme.typography.titleMedium
        )
    }

    itemsIndexed(watchers) { index, item ->
        UserItemWithAction(
            user = item,
            onRemoveClick = { editActions.editWatchers.remove(item) },
            onUserItemClick = { navigateToProfile(item.id) }
        )

        if (index < watchers.lastIndex) {
            Spacer(Modifier.height(6.dp))
        }
    }

    // add watcher & loader
    item {
        if (editActions.editWatchers.isLoading) {
            DotsLoader()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AddButton(
                text = stringResource(Res.string.add_watcher),
                onClick = { showWatchersSelector() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            val (buttonText: StringResource, buttonIcon: DrawableResource) = if (isWatchedByMe) {
                Res.string.unwatch to Res.drawable.ic_unwatch
            } else {
                Res.string.watch to Res.drawable.ic_watch

            }

            TextButton(
                text = stringResource(buttonText),
                icon = buttonIcon,
                onClick = {
                    if (isWatchedByMe) {
                        editActions.editWatch.remove(Unit)
                    } else {
                        editActions.editWatch.select(Unit)
                    }
                }
            )
        }
    }
}

