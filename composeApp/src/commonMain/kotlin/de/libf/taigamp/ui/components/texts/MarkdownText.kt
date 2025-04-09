package de.libf.taigamp.ui.components.texts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

/**
 * Use android TextView because Compose does not support Markdown yet
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    isSelectable: Boolean = true
) {
    Markdown(
        content = text,
        modifier = modifier,
        imageTransformer = Coil3ImageTransformerImpl,
        typography = markdownTypography(),
        colors = markdownColor()
    )
//    if (!::markwon.isInitialized) {
//        markwon = Markwon.builder(LocalContext.current)
//            .usePlugin(CoilImagesPlugin.create(LocalContext.current))
//            .build()
//    }
//    val textSize = MaterialTheme.typography.bodyLarge.fontSize.value
//    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
//    AndroidView(
//        factory = ::TextView,
//        modifier = modifier
//    ) {
//        it.textSize = textSize
//        it.setTextColor(textColor)
//        it.setTextIsSelectable(isSelectable)
//        markwon.setMarkdown(it, text)
//    }
}

// Hold Markwon object (use existing instead of recreating on each recomposition)
//private lateinit var markwon: Markwon
