package de.libf.taigamp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.AuthType
import de.libf.taigamp.domain.repositories.IAuthRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.launch
import taigamultiplatform.composeapp.generated.resources.Res
import taigamultiplatform.composeapp.generated.resources.login_error_message

class LoginViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    val loginResult = MutableResultFlow<Unit>()

    fun login(taigaServer: String, authType: AuthType, username: String, password: String) = viewModelScope.launch {
        loginResult.loadOrError(Res.string.login_error_message) {
            authRepository.auth(taigaServer, authType, password, username)
        }
    }
}
