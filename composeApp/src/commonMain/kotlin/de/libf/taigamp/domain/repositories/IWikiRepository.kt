package de.libf.taigamp.domain.repositories

import de.libf.taigamp.domain.entities.Attachment
import de.libf.taigamp.domain.entities.WikiLink
import de.libf.taigamp.domain.entities.WikiPage
import io.ktor.utils.io.ByteReadChannel

interface IWikiRepository {
    suspend fun getProjectWikiPages(): List<WikiPage>
    suspend fun getProjectWikiPageBySlug(slug: String): WikiPage
    suspend fun editWikiPage(pageId: Long, content: String, version: Int)
    suspend fun deleteWikiPage(pageId: Long)
    suspend fun getPageAttachments(pageId: Long): List<Attachment>
    suspend fun addPageAttachment(pageId: Long, fileName: String, inputStream: ByteReadChannel)
    suspend fun deletePageAttachment(attachmentId: Long)

    suspend fun getWikiLinks(): List<WikiLink>
    suspend fun createWikiLink(href: String, title: String)
    suspend fun deleteWikiLink(linkId: Long)
}