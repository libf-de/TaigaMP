package de.libf.taigamp.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.CommonTaskExtended
import de.libf.taigamp.domain.entities.DueDateStatus
import de.libf.taigamp.ui.components.pickers.DatePicker
import de.libf.taigamp.ui.screens.commontask.EditActions
import de.libf.taigamp.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.ic_clock
import taigamultiplatform.composeapp.generated.resources.no_due_date

@Suppress("FunctionName")
fun LazyListScope.CommonTaskDueDate(
    commonTask: CommonTaskExtended,
    editActions: EditActions
) {
    item {
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        val background = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        val defaultIconBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(background, MaterialTheme.shapes.small)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(
                        color = when (commonTask.dueDateStatus) {
                            DueDateStatus.NotSet, DueDateStatus.NoLongerApplicable, null -> defaultIconBackground
                            DueDateStatus.Set -> taigaGreenPositive
                            DueDateStatus.DueSoon -> taigaOrange
                            DueDateStatus.PastDue -> taigaRed
                        }.takeUnless { editActions.editDueDate.isLoading } ?: defaultIconBackground,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(4.dp)
            ) {
                if (editActions.editDueDate.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clock),
                        contentDescription = null,
                        tint = commonTask.dueDate?.let { onSurfaceColor } ?: MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            DatePicker(
                date = commonTask.dueDate,
                onDatePicked = {
                    editActions.editDueDate.apply {
                        it?.let { select(it) } ?: remove(Unit)
                    }
                },
                hintId = Res.string.no_due_date,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}
