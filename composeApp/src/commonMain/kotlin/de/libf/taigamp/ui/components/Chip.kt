package de.libf.taigamp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.utils.textColor
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Material chip component (rounded rectangle)
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Chip(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.outline,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement.provides(onClick != null)
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(50),
            color = color,
            contentColor = color.textColor(),
            shadowElevation = 1.dp
        ) {
            Box(
                modifier = Modifier.clickable(
                    indication = ripple(),
                    onClick = onClick ?: {},
                    enabled = onClick != null,
                    interactionSource = remember { MutableInteractionSource() }
                ).padding(vertical = 4.dp, horizontal = 10.dp)
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun ChipPreview() = TaigaMobileTheme {
    Box(modifier = Modifier.padding(10.dp)) {
        Chip {
            Text("Testing chip")
        }
    }
}