package de.libf.taigamp

import androidx.compose.ui.window.ComposeUIViewController
import de.libf.taigamp.ui.screens.main.FilePicker
import de.libf.taigamp.ui.screens.main.MainScreen
import io.ktor.utils.io.ByteReadChannel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.getBytes
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIPresentationController
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
    }
}

private val pickerDelegate = object : NSObject(),
    UIDocumentPickerDelegateProtocol,
    UIAdaptivePresentationControllerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>
    ) {
        (didPickDocumentsAtURLs as? List<*>)?.firstOrNull()?.let { _file ->
            (_file as? NSURL)?.let { file ->
                val byteArr = file.dataRepresentation.toByteArray()
                filePicker.filePicked(file.path ?: file.relativeString, ByteReadChannel(byteArr))
            }
        }
    }

    override fun documentPickerWasCancelled(
        controller: UIDocumentPickerViewController
    ) {

    }

    override fun presentationControllerWillDismiss(
        presentationController: UIPresentationController
    ) {
        (presentationController.presentedViewController as? UIDocumentPickerViewController)
            ?.let { documentPickerWasCancelled(it) }
    }
}

private val filePicker: FilePicker = object : FilePicker() {
    override fun requestFile(onFilePicked: (String, ByteReadChannel) -> Unit) {
        super.requestFile(onFilePicked)
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeContent)
        ).apply {
            delegate = pickerDelegate
        }

        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            // Reusing a closed/dismissed picker causes problems with
            // triggering delegate functions, launch with a new one.
            picker,
            animated = true,
            completion = {
                (picker as? UIDocumentPickerViewController)?.allowsMultipleSelection = false
            },
        )
    }
}

fun MainViewController() = ComposeUIViewController { MainScreen(filePicker) }