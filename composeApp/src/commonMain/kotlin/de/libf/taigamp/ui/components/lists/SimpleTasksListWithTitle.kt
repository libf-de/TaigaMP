package de.libf.taigamp.ui.components.lists

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.ui.components.loaders.DotsLoader
import de.libf.taigamp.ui.components.texts.SectionTitle
import de.libf.taigamp.ui.utils.NavigateToTask
import org.jetbrains.compose.resources.StringResource
import app.cash.paging.compose.itemKey
import app.cash.paging.compose.itemContentType
import org.jetbrains.compose.resources.stringResource

/**
 * List of tasks with optional title.
 */
fun LazyListScope.SimpleTasksListWithTitle(
    navigateToTask: NavigateToTask,
    commonTasks: List<CommonTask> = emptyList(),
    commonTasksLazy: LazyPagingItems<CommonTask>? = null,
    keysHash: Int = 0,
    titleText: StringResource? = null,
    topPadding: Dp = 0.dp,
    horizontalPadding: Dp = 0.dp,
    bottomPadding: Dp = 0.dp,
    isTasksLoading: Boolean = false,
    showExtendedTaskInfo: Boolean = false,
    navigateToCreateCommonTask: (() -> Unit)? = null
) {
    val isLoading = commonTasksLazy
        ?.run { loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading }
        ?: isTasksLoading

    item {
        Spacer(Modifier.height(topPadding))
    }

    titleText?.let {
        item {
            SectionTitle(
                text = stringResource(it),
                horizontalPadding = horizontalPadding,
                onAddClick = navigateToCreateCommonTask
            )
        }
    }

    commonTasksLazy?.let {
        items(
            count = it.itemCount,
            key = it.itemKey { item -> item.id + keysHash },
            contentType = it.itemContentType { "CommonTask" }
        ) { index ->
            val item = it[index]
            if (item != null) {
                CommonTaskItem(
                    commonTask = item,
                    horizontalPadding = horizontalPadding,
                    navigateToTask = navigateToTask,
                    showExtendedInfo = showExtendedTaskInfo
                )

                if (index < it.itemCount - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = horizontalPadding),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    } ?: items(commonTasks) { item ->
        CommonTaskItem(
            commonTask = item,
            horizontalPadding = horizontalPadding,
            navigateToTask = navigateToTask,
            showExtendedInfo = showExtendedTaskInfo
        )

        if (commonTasks.indexOf(item) < commonTasks.lastIndex) {
            Divider(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = horizontalPadding),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    item {
        if (isLoading) {
            DotsLoader()
        }
        Spacer(Modifier.height(bottomPadding))
    }
}