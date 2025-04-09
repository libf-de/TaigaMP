package de.libf.taigamp.domain.repositories

import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.Sprint
import kotlinx.datetime.LocalDate

interface ISprintsRepository {
    suspend fun getSprints(page: Int, isClosed: Boolean = false): List<Sprint>
    suspend fun getSprint(sprintId: Long): Sprint

    suspend fun getSprintIssues(sprintId: Long): List<CommonTask>
    suspend fun getSprintUserStories(sprintId: Long): List<CommonTask>
    suspend fun getSprintTasks(sprintId: Long): List<CommonTask>

    suspend fun createSprint(name: String, start: LocalDate, end: LocalDate)
    suspend fun editSprint(sprintId: Long, name: String, start: LocalDate, end: LocalDate)
    suspend fun deleteSprint(sprintId: Long)
}
