package de.libf.taigamp.domain.repositories

import de.libf.taigamp.domain.entities.Stats
import de.libf.taigamp.domain.entities.TeamMember
import de.libf.taigamp.domain.entities.User

interface IUsersRepository {
    suspend fun getMe(): User
    suspend fun getUser(userId: Long): User
    suspend fun getUserStats(userId: Long): Stats
    suspend fun getTeam(): List<TeamMember>
}