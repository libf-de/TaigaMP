package de.libf.taigamp

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun sendBugReport() {

}

actual fun openUrl(url: String) {
    TODO("implement openUrl")
}

actual fun getVersionName(): String = "testing"