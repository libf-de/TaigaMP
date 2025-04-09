package de.libf.taigamp.data.repositories

import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.domain.entities.Stats
import de.libf.taigamp.domain.entities.TeamMember
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.state.Session
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.last

class UsersRepository constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IUsersRepository {
    override suspend fun getMe() = withIO { taigaApi.getMyProfile() }

    override suspend fun getUser(userId: Long) = withIO { taigaApi.getUser(userId) }

    override suspend fun getUserStats(userId: Long): Stats = withIO { taigaApi.getUserStats(userId) }

    override suspend fun getTeam() = withIO {
        val team = async { taigaApi.getProject(session.currentProjectId.last()).members }
        val stats = async {
            taigaApi.getMemberStats(session.currentProjectId.last()).run {
                // calculating total number of points for each id
                (closed_bugs.toList() + closed_tasks.toList() + created_bugs.toList() +
                    iocaine_tasks.toList() + wiki_changes.toList())
                    .mapNotNull { p -> p.first.toLongOrNull()?.let { it to p.second } }
                    .groupBy { it.first }
                    .map { (k, v) -> k to v.sumOf { it.second } }
                    .toMap()
            }
        }

        stats.await().let { stat ->
            team.await().map {
                TeamMember(
                    id = it.id,
                    avatarUrl = it.photo,
                    name = it.full_name_display,
                    role = it.role_name,
                    username = it.username,
                    totalPower = stat[it.id] ?: 0
                )
            }
        }
    }
}