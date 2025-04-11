package de.libf.taigamp.data.repositories

import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.domain.paging.CommonPagingSource
import de.libf.taigamp.domain.repositories.IProjectsRepository
import de.libf.taigamp.state.Session
import kotlinx.coroutines.flow.last

class ProjectsRepository constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IProjectsRepository {

    override suspend fun searchProjects(query: String, page: Int) = withIO {
        handle404 {
            taigaApi.getProjects(
                query = query,
                page = page,
                pageSize = CommonPagingSource.PAGE_SIZE
            )
        }
    }

    override suspend fun getMyProjects() = withIO {
        taigaApi.getProjects(memberId = session.currentUserId.value)
    }

    override suspend fun getUserProjects(userId: Long) = withIO {
        taigaApi.getProjects(memberId = userId)
    }
}