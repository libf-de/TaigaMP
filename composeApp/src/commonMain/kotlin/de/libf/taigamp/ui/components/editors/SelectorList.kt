package de.libf.taigamp.ui.components.editors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import de.libf.taigamp.ui.components.appbars.AppBarWithBackButton
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.utils.onBackPressed
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T : Any> SelectorList(
    titleHintId: StringResource,
    items: List<T> = emptyList(),
    itemsLazy: LazyPagingItems<T>? = null,
    key: ((item: T) -> Any)? = null, // used to preserve position with lazy items
    isVisible: Boolean = false,
    isItemsLoading: Boolean = false,
    isSearchable: Boolean = true,
    searchData: (String) -> Unit = {},
    navigateBack: () -> Unit = {},
    animationDurationMillis: Int = SelectorListConstants.defaultAnimDurationMillis,
    itemContent: @Composable (T) -> Unit
) = AnimatedVisibility(
    visibleState = remember { MutableTransitionState(false) }
        .apply { targetState = isVisible },
    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(animationDurationMillis)),
    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(animationDurationMillis))
) {
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

    onBackPressed(navigateBack)

    val isLoading = itemsLazy
        ?.run { loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading }
        ?: isItemsLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBarWithBackButton(
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isSearchable) {
                        TextFieldWithHint(
                            hintId = titleHintId,
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            onSearchClick = { searchData(query.text) }
                        )
                    } else {
                        Text(stringResource(titleHintId))
                    }
                }
            },
            navigateBack = navigateBack
        )

        LazyColumn {
            itemsLazy?.let {
                items(
                    count = it.itemCount,
                    key = it.itemKey { item -> key?.invoke(item) ?: item.hashCode() },
                    contentType = it.itemContentType { "item" }
                ) { index ->
                    val item = it[index]
                    if (item != null) {
                        itemContent(item)
                        if(index < it.itemCount -1){
                            Divider(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } ?: items(items) { item ->
                itemContent(item)
                if (items.indexOf(item) < items.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            item {
                if (isLoading) {
                    DotsLoader()
                }
                Spacer(Modifier.size(height = 8.dp, width = 0.dp))
            }
        }
    }
}

object SelectorListConstants {
    const val defaultAnimDurationMillis = 200
}