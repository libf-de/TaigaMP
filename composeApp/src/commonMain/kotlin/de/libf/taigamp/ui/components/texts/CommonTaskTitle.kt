package de.libf.taigamp.ui.components.texts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.libf.taigamp.domain.entities.Tag
import de.libf.taigamp.ui.components.Chip
import de.libf.taigamp.ui.theme.TaigaMobileTheme
import de.libf.taigamp.ui.theme.taigaRed
import de.libf.taigamp.ui.utils.textColor
import de.libf.taigamp.ui.utils.toColor
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.title_with_ref_pattern

/**
 * Text with colored dots (indicators) at the end and tags
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommonTaskTitle(
    ref: Int,
    title: String,
    modifier: Modifier = Modifier,
    isInactive: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    indicatorColorsHex: List<String> = emptyList(),
    tags: List<Tag> = emptyList(),
    isBlocked: Boolean = false
) = Column(modifier = modifier) {
    val space = 4.dp

    Text(
        text = buildAnnotatedString {
            if (isInactive) pushStyle(SpanStyle(color = MaterialTheme.colorScheme.outline, textDecoration = TextDecoration.LineThrough))
            append(stringResource(Res.string.title_with_ref_pattern, ref, title))
            if (isInactive) pop()

            append(" ")

            indicatorColorsHex.forEach {
                pushStyle(SpanStyle(color = it.toColor()))
                append("⬤") // 2B24
                pop()
            }
        },
        color = if (isBlocked) taigaRed else textColor,
        style = MaterialTheme.typography.titleMedium
    )

    if (tags.isNotEmpty()) {
        Spacer(Modifier.height(space))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(space),
            verticalArrangement = Arrangement.spacedBy(space),
        ) {
            tags.forEach {
                val bgColor = it.color.toColor()

                Chip(color = bgColor) {
                    Text(
                        text = it.name,
                        color = bgColor.textColor(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CommonTaskTitlePreview() = TaigaMobileTheme {
    CommonTaskTitle(
        ref = 42,
        title = "Some title",
        tags = listOf(Tag("one", "#25A28C"), Tag("two", "#25A28C")),
        isBlocked = true
    )
}

