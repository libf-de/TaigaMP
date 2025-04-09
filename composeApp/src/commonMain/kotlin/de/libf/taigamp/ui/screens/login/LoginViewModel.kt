package de.libf.taigamp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.AuthType
import de.libf.taigamp.domain.repositories.IAuthRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.login_error_message

class LoginViewModel() : ViewModel(), KoinComponent {
    private val authRepository: IAuthRepository by inject()

    val loginResult = MutableResultFlow<Unit>()

    fun login(taigaServer: String, authType: AuthType, username: String, password: String) = viewModelScope.launch {
        Napier.d { "Login with $taigaServer, $authType, $username, $password" }
        loginResult.loadOrError(Res.string.login_error_message) {
            authRepository.auth(taigaServer, authType, password, username)
        }
    }
}
