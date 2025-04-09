package de.libf.taigamp

class DesktopPlatform : Platform {
    override val name: String = "Desktop ${getVersionName()}"
}

actual fun getPlatform(): Platform = DesktopPlatform()

actual fun sendBugReport() {
//    activity.startActivity(
//        Intent(Intent.ACTION_SEND).also {
//            it.type = "text/plain"
//
//            (activity.application as TaigaApp).currentLogFile?.let { file ->
//                it.putExtra(
//                    Intent.EXTRA_STREAM,
//                    FileProvider.getUriForFile(activity, "${activity.packageName}.provider", file)
//                )
//            }
//
//            it.putExtra(Intent.EXTRA_SUBJECT, "Report. Version ${BuildConfig.VERSION_NAME}")
//            it.putExtra(Intent.EXTRA_TEXT, "Android: ${Build.VERSION.RELEASE}\nDevice: ${Build.MODEL}\nDescribe in details your problem:")
//        }
//    )
}

actual fun openUrl(url: String) {
//    activity.startActivity(
//        Intent(
//            Intent.ACTION_VIEW,
//            url.toUri()
//        )
//    )
    TODO("implement openUrl")
}

actual fun getVersionName(): String = "testVersion" //BuildConfig.VERSION_NAME