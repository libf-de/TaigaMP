package de.libf.taigamp.ui.screens.wiki.createpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.WikiPage
import de.libf.taigamp.domain.repositories.IWikiRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WikiCreatePageViewModel() : ViewModel(), KoinComponent {
    private val wikiRepository: IWikiRepository by inject()

    val creationResult = MutableResultFlow<WikiPage>()

    fun createWikiPage(title: String, content: String) = viewModelScope.launch {
        creationResult.loadOrError {
            val slug = title.replace(" ", "-").lowercase()

            wikiRepository.createWikiLink(
                href = slug,
                title = title
            )

            // Need it, because we can't put content to page
            // and create link for it at the same time :(
            val wikiPage = wikiRepository.getProjectWikiPageBySlug(slug)

            wikiRepository.editWikiPage(
                pageId = wikiPage.id,
                content = content,
                version = wikiPage.version
            )

            wikiPage
        }
    }
}