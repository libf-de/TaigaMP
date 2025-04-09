package de.libf.taigamp.ui.utils

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Utility function to handle press on back button
 */
@Composable
actual fun onBackPressed(action: () -> Unit) {
    LocalContext
    (LocalContext.current as? OnBackPressedDispatcherOwner)?.onBackPressedDispatcher?.let { dispatcher ->
        val callback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    action()
                    remove()
                }
            }.also {
                dispatcher.addCallback(it)
            }
        }

        DisposableEffect(Unit) {
            onDispose(callback::remove)
        }
    }
}

//val Context.activity: AppCompatActivity get() = when (this) {
//    is AppCompatActivity -> this
//    is ContextWrapper -> baseContext.activity
//    else -> throw IllegalStateException("Context is not an Activity")
//}