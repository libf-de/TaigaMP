package de.libf.taigamp.ui.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.libf.taigamp.ui.theme.mainHorizontalScreenPadding
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Swipeable tabs
 */

@Composable
fun HorizontalTabbedPager(
    tabs: Array<out Tab>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { tabs.size } ,
    scrollable: Boolean = true,
    edgePadding: Dp = mainHorizontalScreenPadding,
    content: @Composable PagerScope.(page: Int) -> Unit
) = Column(modifier = modifier) {
    val coroutineScope = rememberCoroutineScope()

    val indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
//            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions) TODO: Port
        )
    }

    val tabsRow: @Composable () -> Unit = {
        tabs.forEachIndexed { index, tab ->
            val selected = pagerState.run { targetPage.takeIf { it != currentPage } ?: currentPage == index }
            Tab(
                selected = selected,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                text = {
                    Text(
                        text = stringResource(tab.titleId),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surface,
            indicator = indicator,
            tabs = tabsRow,
            divider = {},
            edgePadding = edgePadding
        )
    } else {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surface,
            indicator = indicator,
            tabs = tabsRow,
            divider = {}
        )
    }

    Spacer(Modifier.height(8.dp))

    HorizontalPager(
        state = pagerState,
        pageContent = content
    )
}

interface Tab {
    val titleId: StringResource
}
