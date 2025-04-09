package de.libf.taigamp.domain.repositories

import de.libf.taigamp.domain.entities.AuthType

interface IAuthRepository {
    suspend fun auth(taigaServer: String, authType: AuthType, password: String, username: String)
}