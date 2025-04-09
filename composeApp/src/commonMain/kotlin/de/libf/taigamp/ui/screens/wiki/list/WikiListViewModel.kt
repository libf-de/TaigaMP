package de.libf.taigamp.ui.screens.wiki.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.WikiLink
import de.libf.taigamp.domain.entities.WikiPage
import de.libf.taigamp.domain.repositories.IWikiRepository
import de.libf.taigamp.state.Session
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.launch

class WikiListViewModel(
    private val session: Session,
    private val wikiRepository: IWikiRepository
) : ViewModel() {
    val projectName by lazy { session.currentProjectName }

    val wikiPages = MutableResultFlow<List<WikiPage>>()
    val wikiLinks = MutableResultFlow<List<WikiLink>>()

    fun onOpen() {
        getWikiPage()
        getWikiLinks()
    }

    fun getWikiPage() = viewModelScope.launch {
        wikiPages.loadOrError {
            wikiRepository.getProjectWikiPages()
        }
    }

    fun getWikiLinks() = viewModelScope.launch {
        wikiLinks.loadOrError {
            wikiRepository.getWikiLinks()
        }
    }
}