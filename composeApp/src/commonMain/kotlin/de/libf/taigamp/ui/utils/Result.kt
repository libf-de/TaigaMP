package de.libf.taigamp.ui.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

/**
 * Convenient way to dispatch events
 */
sealed class Result<T>(
    val data: T? = null,
    val message: StringResource? = null
)

class SuccessResult<T>(data: T?) : Result<T>(data = data)
class ErrorResult<T>(message: StringResource? = null) : Result<T>(message = message)
class LoadingResult<T>(data: T? = null) : Result<T>(data = data)
class NothingResult<T> : Result<T>()

typealias MutableResultFlow<T> = MutableStateFlow<Result<T>>
fun <T> MutableResultFlow(value: Result<T> = NothingResult()) = MutableStateFlow(value)
typealias ResultFlow<T> = StateFlow<Result<T>>
