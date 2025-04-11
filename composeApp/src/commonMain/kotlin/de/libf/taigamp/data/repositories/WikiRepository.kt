package de.libf.taigamp.data.repositories

import de.libf.taigamp.data.api.CommonTaskPathPlural
import de.libf.taigamp.data.api.EditWikiPageRequest
import de.libf.taigamp.data.api.NewWikiLinkRequest
import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.domain.entities.Attachment
import de.libf.taigamp.domain.repositories.IWikiRepository
import de.libf.taigamp.state.Session
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.readFully
import kotlinx.coroutines.flow.last

class WikiRepository constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IWikiRepository {
    override suspend fun getProjectWikiPages() = withIO {
        taigaApi.getProjectWikiPages(
            projectId = session.currentProjectId.value
        )
    }

    override suspend fun getProjectWikiPageBySlug(slug: String) = withIO {
        taigaApi.getProjectWikiPageBySlug(
            projectId = session.currentProjectId.value,
            slug = slug
        )
    }

    override suspend fun editWikiPage(pageId: Long, content: String, version: Int) = withIO {
        taigaApi.editWikiPage(
            pageId = pageId,
            editWikiPageRequest = EditWikiPageRequest(content, version)
        )
    }

    override suspend fun deleteWikiPage(pageId: Long) = withIO {
        taigaApi.deleteWikiPage(
            pageId = pageId
        )
        return@withIO
    }

    override suspend fun getPageAttachments(pageId: Long): List<Attachment> = withIO {
        taigaApi.getPageAttachments(
            pageId = pageId,
            projectId = session.currentProjectId.value
        )
    }

    override suspend fun addPageAttachment(pageId: Long, fileName: String, inputStream: ByteReadChannel) = withIO {
        val fileSize = inputStream.availableForRead // Get the size of the stream
        val fileByteArray = ByteArray(fileSize) //create an array of the correct size.
        inputStream.readFully(fileByteArray, 0, fileSize)

        val projectId = session.currentProjectId.value

        val multiPartContent = MultiPartFormDataContent(
            formData {
                append(
                    key = "attached_file",
                    value = fileByteArray,
                    headers = headersOf(HttpHeaders.ContentDisposition, "form-data; name=\"attached_file\"; filename=\"$fileName\"")
                )
                append("project", projectId.toString())
                append("object_id", pageId.toString())
            }
        )

        taigaApi.uploadPageAttachment(
            multipartBody = multiPartContent
        )
    }

    override suspend fun deletePageAttachment(attachmentId: Long) = withIO {
        taigaApi.deletePageAttachment(
            attachmentId = attachmentId
        )
        return@withIO
    }

    override suspend fun getWikiLinks() = withIO {
        taigaApi.getWikiLink(
            projectId = session.currentProjectId.value
        )
    }

    override suspend fun createWikiLink(href: String, title: String) = withIO {
        taigaApi.createWikiLink(
            newWikiLinkRequest = NewWikiLinkRequest(
                href = href,
                project = session.currentProjectId.value,
                title = title
            )
        )
    }

    override suspend fun deleteWikiLink(linkId: Long) = withIO {
        taigaApi.deleteWikiLink(
            linkId = linkId
        )
        return@withIO
    }
}