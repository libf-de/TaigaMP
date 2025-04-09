package de.libf.taigamp.ui.utils

import com.diamondedge.logging.logging
import org.jetbrains.compose.resources.StringResource
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.common_error_message

internal inline fun <T> MutableResultFlow<T>.loadOrError(
    messageId: StringResource = Res.string.common_error_message,
    preserveValue: Boolean = true,
    showLoading: Boolean = true,
    load: () -> T?
) {
    if (showLoading) {
        value = LoadingResult(value.data.takeIf { preserveValue })
    }

    value = try {
        SuccessResult(load())
    } catch (e: Exception) {
        logging("loadOrError").e { e }
        ErrorResult(messageId)
    }
}

///**
// * Convert Flow to instance of LazyPagingItems
// * TODO fix of https://issuetracker.google.com/issues/177245496
// */
//@Suppress("UNCHECKED_CAST")
//fun <T : Any> Flow<PagingData<T>>.asLazyPagingItems(scope: CoroutineScope) = cachedIn(scope).let { flow ->
//    // yep, working with instance of LazyPagingItems via reflection
//    LazyPagingItems::class.constructors.toList().first().run {
//        call(flow) as LazyPagingItems<T>
//    }.also { items ->
//        scope.launch {
//            LazyPagingItems::class.declaredFunctions.find { it.name == "collectPagingData" }!!.apply {
//                callSuspend(items)
//            }
//        }
//        scope.launch {
//            LazyPagingItems::class.declaredFunctions.find { it.name == "collectLoadState" }!!.apply {
//                callSuspend(items)
//            }
//        }
//    }
//}