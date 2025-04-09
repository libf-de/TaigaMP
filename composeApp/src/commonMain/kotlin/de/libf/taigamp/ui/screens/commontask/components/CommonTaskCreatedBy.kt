package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import de.libf.taigamp.domain.entities.CommonTaskExtended
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.ui.components.lists.UserItem
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.created_by

@Suppress("FunctionName")
fun LazyListScope.CommonTaskCreatedBy(
    creator: User,
    commonTask: CommonTaskExtended,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.created_by),
            style = MaterialTheme.typography.titleMedium
        )

        UserItem(
            user = creator,
            dateTime = commonTask.createdDateTime,
            onUserItemClick = { navigateToProfile(creator.id) }
        )
    }
}
