package de.libf.taigamp.data.repositories

import de.libf.taigamp.state.Session
import de.libf.taigamp.data.api.AuthRequest
import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.domain.entities.AuthType
import de.libf.taigamp.domain.repositories.IAuthRepository

class AuthRepository constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IAuthRepository {
    override suspend fun auth(taigaServer: String, authType: AuthType, password: String, username: String) = withIO {
        session.changeServer(taigaServer)
        taigaApi.auth(
            AuthRequest(
                username = username,
                password = password,
                type = when (authType) {
                    AuthType.Normal -> "normal"
                    AuthType.LDAP -> "ldap"
                }
            )
        ).let {
            session.changeAuthCredentials(
                token = it.auth_token,
                refreshToken = it.refresh ?: "missing" // compatibility with older Taiga versions without refresh token
            )
            session.changeCurrentUserId(it.id)
        }
    }
}
