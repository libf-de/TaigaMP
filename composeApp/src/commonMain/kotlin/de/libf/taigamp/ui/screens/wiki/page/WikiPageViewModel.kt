package de.libf.taigamp.ui.screens.wiki.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.Attachment
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.domain.entities.WikiLink
import de.libf.taigamp.domain.entities.WikiPage
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.domain.repositories.IWikiRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.permission_error

class WikiPageViewModel(
    private val wikiRepository: IWikiRepository,
    private val userRepository: IUsersRepository
) : ViewModel() {
    private lateinit var pageSlug: String

    val page = MutableResultFlow<WikiPage>()
    val link = MutableResultFlow<WikiLink>()
    val attachments = MutableResultFlow<List<Attachment>>()
    val editWikiPageResult = MutableResultFlow<Unit>()
    val deleteWikiPageResult = MutableResultFlow<Unit>()

    var lastModifierUser = MutableStateFlow<User?>(null)

    fun onOpen(slug: String) {
        pageSlug = slug
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        page.loadOrError {
            wikiRepository.getProjectWikiPageBySlug(pageSlug).also {

                lastModifierUser.value = userRepository.getUser(it.lastModifier)

                val jobsToLoad = arrayOf(
                    launch {
                        link.loadOrError(showLoading = false) {
                            wikiRepository.getWikiLinks().find { it.ref == pageSlug }
                        }
                    },
                    launch {
                        attachments.loadOrError(showLoading = false) {
                            wikiRepository.getPageAttachments(it.id)
                        }
                    }
                )

                joinAll(*jobsToLoad)
            }
        }
    }

    fun deleteWikiPage() = viewModelScope.launch {
        deleteWikiPageResult.loadOrError {
            val linkId = link.value.data?.id
            val pageId = page.value.data?.id

            pageId?.let { wikiRepository.deleteWikiPage(it) }
            linkId?.let { wikiRepository.deleteWikiLink(it) }
        }
    }

    fun editWikiPage(content: String) = viewModelScope.launch {
        editWikiPageResult.loadOrError {
            page.value.data?.let {
                wikiRepository.editWikiPage(
                    pageId = it.id,
                    content = content,
                    version = it.version
                )

                loadData().join()
            }
        }
    }

    fun deletePageAttachment(attachment: Attachment) = viewModelScope.launch {
        attachments.loadOrError(Res.string.permission_error) {
            wikiRepository.deletePageAttachment(
                attachmentId = attachment.id
            )

            loadData().join()
            attachments.value.data
        }
    }

    fun addPageAttachment(fileName: String, inputStream: ByteReadChannel) = viewModelScope.launch {
        attachments.loadOrError(Res.string.permission_error) {
            page.value.data?.id?.let { pageId ->
                wikiRepository.addPageAttachment(
                    pageId = pageId,
                    fileName = fileName,
                    inputStream = inputStream
                )
                loadData().join()
            }
            attachments.value.data
        }
    }
}