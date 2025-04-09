package de.libf.taigamp.ui.components.editors

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * You've read it right. Text field. With hint.
 */
@Composable
fun TextFieldWithHint(
    hintId: StringResource,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp,
    width: Dp? = null,
    minHeight: Dp? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusChange: (Boolean) -> Unit = {},
    focusRequester: FocusRequester = remember { FocusRequester() },
    maxLines: Int = Int.MAX_VALUE,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onSearchClick: (() -> Unit)? = null,
    hasBorder: Boolean = false,
    contentAlignment: Alignment = Alignment.CenterStart
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val unfocusedColor = MaterialTheme.colorScheme.outline
    var outlineColor by remember { mutableStateOf(unfocusedColor) }

    Box(
        contentAlignment = contentAlignment,
        modifier = Modifier.let { m -> width?.let { m.width(it) } ?: m.fillMaxWidth() }
            .heightIn(min = minHeight ?: Dp.Unspecified)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .let {
                if (hasBorder) {
                    it.border(width = 1.dp, color = outlineColor, shape = MaterialTheme.shapes.large)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                } else {
                    it
                }
            }
    ) {
        if (value.text.isEmpty()) {
            Text(
                text = stringResource(hintId),
                style = style,
                color = unfocusedColor
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    onFocusChange(it.isFocused)
                    outlineColor = if (it.isFocused) primaryColor else unfocusedColor
                },
            textStyle = style.merge(TextStyle(color = textColor)),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                imeAction = onSearchClick?.let { ImeAction.Search } ?: ImeAction.Default,
                keyboardType = keyboardType
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick?.invoke() })
        )
    }
}

val searchFieldHorizontalPadding = mainHorizontalScreenPadding
val searchFieldVerticalPadding = 8.dp
