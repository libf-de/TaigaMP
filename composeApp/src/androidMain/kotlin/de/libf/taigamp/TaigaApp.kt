package de.libf.taigamp

import android.app.Application
import de.libf.taigamp.di.dataModule
import de.libf.taigamp.di.platformModule
import de.libf.taigamp.di.repoModule
import de.libf.taigamp.di.viewModelModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TaigaApp : Application() {

    // logging
//    private var fileLoggingTree: FileLoggingTree? = null
//    val currentLogFile get() = fileLoggingTree?.currentFile


    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        startKoin {
            androidContext(this@TaigaApp)
            androidLogger()
            modules(dataModule, repoModule, viewModelModule, platformModule)
        }

//        KmLogging.setLogLevel(if (BuildConfig.DEBUG) LogLevel.Verbose else LogLevel.Off)
//
//        // logging configs
//        val minLoggingPriority = if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//            Log.DEBUG
//        } else {
//            Log.WARN
//        }
//
//        try {
//            fileLoggingTree = FileLoggingTree(applicationContext.getExternalFilesDir("logs")!!.absolutePath, minLoggingPriority)
//            Timber.plant(fileLoggingTree!!)
//        } catch (e: NullPointerException) {
//            Timber.w("Cannot setup FileLoggingTree, skipping")
//        }
//
//        // Apply dynamic color
//        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}