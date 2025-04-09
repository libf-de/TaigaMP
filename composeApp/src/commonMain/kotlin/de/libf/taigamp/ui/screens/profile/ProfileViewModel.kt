package de.libf.taigamp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.Project
import de.libf.taigamp.domain.entities.Stats
import de.libf.taigamp.domain.entities.User
import de.libf.taigamp.domain.repositories.IProjectsRepository
import de.libf.taigamp.domain.repositories.IUsersRepository
import de.libf.taigamp.state.Session
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val usersRepository: IUsersRepository,
    private val projectsRepository: IProjectsRepository,
    private val session: Session
) : ViewModel() {
    val currentUser = MutableResultFlow<User>()
    val currentUserStats = MutableResultFlow<Stats>()
    val currentUserProjects = MutableResultFlow<List<Project>>()
    val currentProjectId by lazy { session.currentProjectId }


    fun onOpen(userId: Long) = viewModelScope.launch {
        currentUser.loadOrError { usersRepository.getUser(userId) }
        currentUserStats.loadOrError { usersRepository.getUserStats(userId) }
        currentUserProjects.loadOrError { projectsRepository.getUserProjects(userId) }
    }
}