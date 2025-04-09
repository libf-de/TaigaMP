package de.libf.taigamp.ui.components.texts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.libf.taigamp.R

/**
 * Title with optional add button
 */

@Composable
fun SectionTitle(
    text: String,
    horizontalPadding: Dp = 0.dp,
    bottomPadding: Dp = 6.dp,
    onAddClick: (() -> Unit)? = null
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
        .height(IntrinsicSize.Min)
        .fillMaxWidth()
        .padding(horizontal = horizontalPadding)
        .padding(bottom = bottomPadding)
        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(6.dp)
    )

    onAddClick?.let {
        Box(
            modifier = Modifier.fillMaxHeight()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    onClick = it,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                )
                .padding(6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
