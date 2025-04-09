package de.libf.taigamp.ui.screens.main

import androidx.compose.runtime.staticCompositionLocalOf
import io.ktor.utils.io.ByteReadChannel

/**
 * Way to pick files from composables
 */
abstract class FilePicker {
    private var onFilePicked: (String, ByteReadChannel) -> Unit = { _, _ -> }

    open fun requestFile(onFilePicked: (String, ByteReadChannel) -> Unit) {
        this.onFilePicked = onFilePicked
    }

    fun filePicked(name: String, inputStream: ByteReadChannel) = onFilePicked(name, inputStream)
}

val LocalFilePicker = staticCompositionLocalOf<FilePicker> { error("No FilePicker provided") }
