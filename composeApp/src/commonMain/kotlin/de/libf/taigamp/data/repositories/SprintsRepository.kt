package de.libf.taigamp.data.repositories

import de.libf.taigamp.state.Session
import de.libf.taigamp.data.api.CreateSprintRequest
import de.libf.taigamp.data.api.EditSprintRequest
import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.repositories.ISprintsRepository
import kotlinx.coroutines.flow.last
import kotlinx.datetime.LocalDate

class SprintsRepository constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : ISprintsRepository {
    override suspend fun getSprintUserStories(sprintId: Long) = withIO {
        taigaApi.getUserStories(project = session.currentProjectId.value, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.UserStory) }
    }

    override suspend fun getSprints(page: Int, isClosed: Boolean) = withIO {
        handle404 {
            taigaApi.getSprints(session.currentProjectId.value, page, isClosed).map { it.toSprint() }
        }
    }

    override suspend fun getSprint(sprintId: Long) = withIO {
        taigaApi.getSprint(sprintId).toSprint()
    }

    override suspend fun getSprintTasks(sprintId: Long) = withIO {
        taigaApi.getTasks(userStory = "null", project = session.currentProjectId.value, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.Task) }
    }

    override suspend fun getSprintIssues(sprintId: Long) = withIO {
        taigaApi.getIssues(project = session.currentProjectId.value, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.Issue) }

    }

    override suspend fun createSprint(name: String, start: LocalDate, end: LocalDate) = withIO {
        taigaApi.createSprint(CreateSprintRequest(name, start, end, session.currentProjectId.value))
    }

    override suspend fun editSprint(
        sprintId: Long,
        name: String,
        start: LocalDate,
        end: LocalDate
    ) = withIO {
        taigaApi.editSprint(
            id = sprintId,
            request = EditSprintRequest(name, start, end)
        )
    }

    override suspend fun deleteSprint(sprintId: Long) = withIO {
        taigaApi.deleteSprint(sprintId)
        return@withIO
    }
}
