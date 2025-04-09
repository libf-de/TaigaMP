package de.libf.taigamp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun sendBugReport()

expect fun openUrl(url: String)

expect fun getVersionName(): String