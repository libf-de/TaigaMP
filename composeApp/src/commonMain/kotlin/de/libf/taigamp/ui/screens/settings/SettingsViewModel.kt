package de.libf.taigamp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.state.*
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel() : ViewModel(), KoinComponent {
    private val session: Session by inject()
    private val settings: Settings by inject()
    private val userRepository: IUsersRepository by inject()

    val user = MutableResultFlow<User>()
    val serverUrl by lazy { session.server }

    val themeSetting by lazy { settings.themeSetting }

    fun onOpen() = viewModelScope.launch {
        user.loadOrError(preserveValue = false) { userRepository.getMe() }
    }

    fun logout() {
        viewModelScope.launch { session.reset() }
    }

    fun switchTheme(theme: ThemeSetting) {
        viewModelScope.launch { settings.changeThemeSetting(theme) }
    }
}
