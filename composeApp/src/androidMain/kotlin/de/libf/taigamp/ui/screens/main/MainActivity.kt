package de.libf.taigamp.ui.screens.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel

class MainActivity : ComponentActivity() {

    @SuppressLint("Range")
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        val inputStream = contentResolver.openInputStream(it) ?: return@registerForActivityResult
        val fileName = contentResolver.query(it, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        } ?: return@registerForActivityResult

        filePicker.filePicked(fileName, inputStream.toByteReadChannel())
    }

    private val filePicker: FilePicker = object : FilePicker() {
        override fun requestFile(onFilePicked: (String, ByteReadChannel) -> Unit) {
            super.requestFile(onFilePicked)
            getContent.launch("*/*")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MainScreen(filePicker)
        }
    }
}