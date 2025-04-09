package de.libf.taigamp.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.state.Session
import de.libf.taigamp.domain.entities.TeamMember
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.NothingResult
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TeamViewModel() : ViewModel(), KoinComponent {
    private val usersRepository: IUsersRepository by inject()
    private val session: Session by inject()

    val projectName by lazy { session.currentProjectName }
    val team = MutableResultFlow<List<TeamMember>?>()

    private var shouldReload = true

    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            team.loadOrError { usersRepository.getTeam() }
        }
        shouldReload = false
    }

    init {
        session.currentProjectId.onEach {
            team.value = NothingResult()
            shouldReload = true
        }.launchIn(viewModelScope)
    }
}
