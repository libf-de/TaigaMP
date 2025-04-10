package de.libf.taigamp

import androidx.compose.ui.window.ComposeUIViewController
import de.libf.taigamp.ui.screens.main.FilePicker
import de.libf.taigamp.ui.screens.main.MainScreen
import io.ktor.utils.io.ByteReadChannel

private val filePicker: FilePicker = object : FilePicker() {
    override fun requestFile(onFilePicked: (String, ByteReadChannel) -> Unit) {
        super.requestFile(onFilePicked)
    }
}

fun MainViewController() = ComposeUIViewController { MainScreen(filePicker) }