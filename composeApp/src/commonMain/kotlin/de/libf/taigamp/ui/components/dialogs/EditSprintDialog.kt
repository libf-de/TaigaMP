package de.libf.taigamp.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.components.editors.TextFieldWithHint
import de.libf.taigamp.ui.components.pickers.DatePicker
import de.libf.taigamp.ui.utils.now
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.cancel
import taigamultiplatform.composeapp.generated.resources.ok
import taigamultiplatform.composeapp.generated.resources.sprint_name_hint

@Composable
fun EditSprintDialog(
    initialName: String = "",
    initialStart: LocalDate? = null,
    initialEnd: LocalDate? = null,
    onConfirm: (name: String, start: LocalDate, end: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(initialName)) }
    var start by remember { mutableStateOf(initialStart ?: LocalDate.now()) }
    var end by remember { mutableStateOf(initialEnd ?: LocalDate.now().plus(DatePeriod(days = 14))) }

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.cancel),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    name.text.trim()
                        .takeIf { it.isNotEmpty() }
                        ?.let { onConfirm(it, start, end) }
                }
            ) {
                Text(
                    text = stringResource(Res.string.ok),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        title = {
            TextFieldWithHint(
                hintId = Res.string.sprint_name_hint,
                value = name,
                onValueChange = { name = it },
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            val pickerStyle = MaterialTheme.typography.titleMedium.merge(TextStyle(fontWeight = FontWeight.Normal))
            val pickerModifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.small
                )
                .padding(6.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                DatePicker(
                    date = start,
                    onDatePicked = { start = it!! },
                    showClearButton = false,
                    style = pickerStyle,
                    modifier = pickerModifier
                )

                Spacer(
                    Modifier
                        .width(16.dp)
                        .height(1.5.dp)
                        .background(MaterialTheme.colorScheme.onSurface))

                DatePicker(
                    date = end,
                    onDatePicked = { end = it!! },
                    showClearButton = false,
                    style = pickerStyle,
                    modifier = pickerModifier
                )
            }
        }
    )
}
