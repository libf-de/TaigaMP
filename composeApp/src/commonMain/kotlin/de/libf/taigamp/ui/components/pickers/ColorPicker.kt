package de.libf.taigamp.ui.components.pickers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import de.libf.taigamp.ui.utils.clickableUnindicated
import org.jetbrains.compose.resources.stringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.cancel
import taigamultiplatform.composeapp.generated.resources.ok
import taigamultiplatform.composeapp.generated.resources.select_color

/**
 * Color picker with material dialog
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorPicker(
    size: Dp,
    color: Color,
    onColorPicked: (Color) -> Unit = {}
) {
    val dialogState = rememberMaterialDialogState()

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            // TODO update buttons to comply with material3 color schema?
            positiveButton(text = stringResource(Res.string.ok))
            negativeButton(text = stringResource(Res.string.cancel))
        }
    ) {
        title(stringResource(Res.string.select_color))

        colorChooser(
            colors = (listOf(color) + ColorPalette.Primary).toSet().toList(),
            onColorSelected = onColorPicked,
            argbPickerState = ARGBPickerState.WithoutAlphaSelector
        )
    }

    Spacer(
        Modifier.size(size)
            .background(color = color, shape = MaterialTheme.shapes.small)
            .clickableUnindicated { dialogState.show() }
    )
}
