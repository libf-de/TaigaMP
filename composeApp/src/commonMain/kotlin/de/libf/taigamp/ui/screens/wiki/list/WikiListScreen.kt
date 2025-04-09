package de.libf.taigamp.ui.screens.wiki.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.libf.taigamp.ui.components.appbars.ClickableAppBar
import de.libf.taigamp.ui.components.buttons.PlusButton
import de.libf.taigamp.ui.components.containers.ContainerBox
import de.libf.taigamp.ui.components.containers.HorizontalTabbedPager
import de.libf.taigamp.ui.components.containers.Tab
import de.libf.taigamp.ui.components.loaders.CircularLoader
import de.libf.taigamp.ui.screens.main.Routes
import de.libf.taigamp.ui.utils.LoadingResult
import de.libf.taigamp.ui.utils.navigateToWikiPageScreen
import de.libf.taigamp.ui.utils.subscribeOnError
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.all_wiki_pages
import taigamultiplatform.composeapp.generated.resources.bookmarks

@Composable
fun WikiListScreen(
    navController: NavController,
    showMessage: (StringResource) -> Unit = {},
) {
    val viewModel: WikiListViewModel = viewModel()

    val projectName by viewModel.projectName.collectAsState("")

    val wikiLinks by viewModel.wikiLinks.collectAsState()
    wikiLinks.subscribeOnError(showMessage)

    val wikiPages by viewModel.wikiPages.collectAsState()
    wikiPages.subscribeOnError(showMessage)

    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val wikiPagesSlug = wikiPages.data.orEmpty().map { it.slug }

    WikiListScreenContent(
        projectName = projectName,
        bookmarks = wikiLinks.data.orEmpty().filter { it.ref in wikiPagesSlug }.map { it.title to it.ref },
        allPages = wikiPagesSlug,
        isLoading = wikiLinks is LoadingResult || wikiPages is LoadingResult,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateToCreatePage = {
            navController.navigate(Routes.wiki_create_page)
        },
        navigateToPageBySlug = { slug ->
            navController.navigateToWikiPageScreen(slug)
        },
        navigateBack = { navController.popBackStack() }
    )
}

@Composable
fun WikiListScreenContent(
    projectName: String,
    bookmarks: List<Pair<String, String>> = emptyList(),
    allPages: List<String> = emptyList(),
    isLoading: Boolean = false,
    onTitleClick: () -> Unit = {},
    navigateToCreatePage: () -> Unit = {},
    navigateToPageBySlug: (slug: String) -> Unit = {},
    navigateBack: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ClickableAppBar(
        projectName = projectName,
        actions = {
            PlusButton(
                onClick = navigateToCreatePage
            )
        },
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularLoader()
        }
    }

    if (bookmarks.isEmpty() && allPages.isEmpty()) {
        EmptyWikiDialog(
            createNewPage = navigateToCreatePage
        )
    }

    HorizontalTabbedPager(
        tabs = WikiTabs.values(),
        modifier = Modifier.fillMaxSize(),
        pagerState = rememberPagerState { WikiTabs.values().size }
    ) { page ->
        when (WikiTabs.values()[page]) {
            WikiTabs.Bookmarks -> WikiSelectorList(
                titles = bookmarks.map { it.first },
                bookmarks = bookmarks,
                onClick = navigateToPageBySlug
            )
            WikiTabs.AllWikiPages -> WikiSelectorList(
                titles = allPages,
                onClick = navigateToPageBySlug
            )
        }
    }
}

private enum class WikiTabs(override val titleId: StringResource) : Tab {
    Bookmarks(Res.string.bookmarks),
    AllWikiPages(Res.string.all_wiki_pages)
}

@Composable
private fun WikiSelectorList(
    titles: List<String> = emptyList(),
    bookmarks: List<Pair<String, String>> = emptyList(),
    onClick: (name: String) -> Unit = {}
) = Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopStart
) {
    val listItemContent: @Composable LazyItemScope.(Int, String) -> Unit = lambda@{ index, item ->
        WikiSelectorItem(
            title = item,
            onClick = { onClick(bookmarks.getOrNull(index)?.second ?: item) }
        )

        if (index < titles.lastIndex) {
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            itemsIndexed(titles, itemContent = listItemContent)
        }
    }

    if (titles.isEmpty()) {
        EmptyWikiDialog(
            isButtonAvailable = false
        )
    }
}

@Composable
private fun WikiSelectorItem(
    title: String,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(0.8f)) {
            Text(
                text = title
            )
        }
    }
}

@Preview
@Composable
fun WikiPageSelectorPreview() {
    WikiListScreenContent(
        projectName = "Cool project"
    )
}

