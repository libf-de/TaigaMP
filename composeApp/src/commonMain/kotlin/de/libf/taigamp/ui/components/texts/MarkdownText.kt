package de.libf.taigamp.ui.components.texts

import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.image.coil.CoilImagesPlugin

/**
 * Use android TextView because Compose does not support Markdown yet
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = true
) {
    if (!::markwon.isInitialized) {
        markwon = Markwon.builder(LocalContext.current)
            .usePlugin(CoilImagesPlugin.create(LocalContext.current))
            .build()
    }
    val textSize = MaterialTheme.typography.bodyLarge.fontSize.value
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    AndroidView(
        factory = ::TextView,
        modifier = modifier
    ) {
        it.textSize = textSize
        it.setTextColor(textColor)
        it.setTextIsSelectable(isSelectable)
        markwon.setMarkdown(it, text)
    }
}

// Hold Markwon object (use existing instead of recreating on each recomposition)
private lateinit var markwon: Markwon
