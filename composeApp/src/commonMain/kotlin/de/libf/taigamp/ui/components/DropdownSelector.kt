package de.libf.taigamp.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import de.libf.taigamp.ui.theme.dialogTonalElevation
import de.libf.taigamp.ui.utils.clickableUnindicated
import org.jetbrains.compose.resources.painterResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.ic_arrow_down

/**
 * Dropdown selector with animated arrow
 */

@Composable
fun <T> DropdownSelector(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
    selectedItemContent: @Composable (T) -> Unit,
    takeMaxWidth: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    tint: Color = MaterialTheme.colorScheme.primary,
    onExpanded: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val transitionState = remember { MutableTransitionState(isExpanded) }
    transitionState.targetState = isExpanded

    if (isExpanded) onExpanded()
    
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
            modifier = Modifier
                .let { if (takeMaxWidth) it.fillMaxWidth() else it }
                .clickableUnindicated {
                    isExpanded = !isExpanded
                }
        ) {

            selectedItemContent(selectedItem)

            val arrowRotation by rememberTransition(
                transitionState,
                label = "arrow"
            ).animateFloat { if (it) -180f else 0f }

            Icon(
                painter = painterResource(Res.drawable.ic_arrow_down),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.rotate(arrowRotation)
            )
        }

        DropdownMenu(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
            ),
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
                onDismissRequest()
            }
        ) {
            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onItemSelected(it)
                    },
                    text = {
                        itemContent(it)
                    }
                )
            }
        }
    }
}
