package de.libf.taigamp.ui.utils


import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import org.jetbrains.compose.resources.StringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.common_error_message
import kotlin.math.ln

@Composable
expect fun onBackPressed(action: () -> Unit);


fun Modifier.clickableUnindicated(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClickLabel = null,
        role = null,
        onClick
    )
}


// Error functions
@Composable
inline fun Result<*>.subscribeOnError(crossinline onError: (message: StringResource) -> Unit) = (this as? ErrorResult)?.message?.let {
    LaunchedEffect(this) {
        onError(it)
    }
}

@Composable
internal inline fun <T : Any> LazyPagingItems<T>.subscribeOnError(crossinline onError: (message: StringResource) -> Unit) {
    if (loadState.run { listOf(refresh, prepend, append) }.any { it is LoadState.Error }) {
        LaunchedEffect(this) {
            onError(Res.string.common_error_message)
        }
    }
}


// Color functions
fun String.toColor(): Color {
    val hex = if (startsWith("#")) {
        substring(1)
    } else {
        this
    }

    return when (hex.length) {
        3 -> { // RGB
            val r = hex[0].digitToInt(16) * 17
            val g = hex[1].digitToInt(16) * 17
            val b = hex[2].digitToInt(16) * 17
            Color(r, g, b)
        }
        4 -> { // RGBA
            val r = hex[0].digitToInt(16) * 17
            val g = hex[1].digitToInt(16) * 17
            val b = hex[2].digitToInt(16) * 17
            val a = hex[3].digitToInt(16) * 17
            Color(r, g, b, a)
        }
        6 -> { // RRGGBB
            Color(hex.substring(0, 2).toInt(16),
                hex.substring(2, 4).toInt(16),
                hex.substring(4, 6).toInt(16))
        }
        8 -> { // RRGGBBAA
            Color(hex.substring(0, 2).toInt(16),
                hex.substring(2, 4).toInt(16),
                hex.substring(4, 6).toInt(16),
                hex.substring(6, 8).toInt(16))
        }
        else -> {
            // Invalid format, return a default color (e.g., transparent)
            Color.Transparent
        }
    }
}


fun Color.toHex(): String {
    val argb = toArgb()
    val alpha = (argb shr 24) and 0xFF
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF

    return buildString {
        if (alpha != 255) {
            append("#")
            append(alpha.toHexComponent())
        } else {
            append("#")
        }
        append(red.toHexComponent())
        append(green.toHexComponent())
        append(blue.toHexComponent())
    }
}

private fun Int.toHexComponent(): String = toString(16).padStart(2, '0').uppercase()

// calculate optimal text color for colored background background
fun Color.textColor() = if (luminance() < 0.5) Color.White else Color.Black
// copy from library, because it is internal in library
//fun ColorScheme.surfaceColorAtElevation(elevation: Dp): Color {
//    if (elevation == 0.dp) return surface
//    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
//    return primary.copy(alpha = alpha).compositeOver(surface)
//}

fun String.isValidUrl(): Boolean {
    val urlRegex = Regex(
        "^((https?|ftp)://)?([\\w.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?\$"
    )

    return urlRegex.matches(this)
}

//fun Modifier.Companion.navigationBarsHeight(dp: Dp): Modifier {
//    return this.size(width = 128.dp, height = dp)
//}