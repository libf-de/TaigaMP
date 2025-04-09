package de.libf.taigamp.ui.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

val Context.activity: ComponentActivity
    get() = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.activity
    else -> throw IllegalStateException("Context is not an Activity")
}