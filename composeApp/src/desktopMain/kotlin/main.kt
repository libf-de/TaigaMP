import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.libf.taigamp.di.dataModule
import de.libf.taigamp.di.platformModule
import de.libf.taigamp.di.repoModule
import de.libf.taigamp.di.viewModelModule
import de.libf.taigamp.ui.screens.main.FilePicker
import de.libf.taigamp.ui.screens.main.MainScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.app_name
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


fun chooseFile(
    initialDirectory: String,
    fileExtension: String,
    title: String?
): String? = MemoryStack.stackPush().use { stack ->
    val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
    val aFilterPatterns = stack.mallocPointer(filters.size)
    filters.forEach {
        aFilterPatterns.put(stack.UTF8("*.$it"))
    }
    aFilterPatterns.flip()
    TinyFileDialogs.tinyfd_openFileDialog(
        title,
        initialDirectory,
        null,
        null,
        false
    )
}

private fun pickFile() {
    val fileName = chooseFile(".", "*", "Pick file")
    fileName?.let {
        val initialFile = File(it)
        filePicker.filePicked(
            name = it,
            inputStream = FileInputStream(initialFile).toByteReadChannel()
        )
    }
}


private val filePicker: FilePicker = object : FilePicker() {
    override fun requestFile(onFilePicked: (String, ByteReadChannel) -> Unit) {
        super.requestFile(onFilePicked)
        pickFile()
    }
}

fun main() = application {
    Napier.base(DebugAntilog())

    startKoin {
        modules(dataModule, repoModule, viewModelModule, platformModule)
    }

    Napier.d("hello")

    Window(onCloseRequest = ::exitApplication, title = stringResource(Res.string.app_name)) {
        MainScreen(filePicker)
    }
}