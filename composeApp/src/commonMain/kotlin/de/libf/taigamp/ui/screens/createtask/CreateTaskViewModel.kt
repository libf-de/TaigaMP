package de.libf.taigamp.ui.screens.createtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.libf.taigamp.domain.entities.CommonTask
import de.libf.taigamp.domain.entities.CommonTaskType
import de.libf.taigamp.domain.repositories.ITasksRepository
import de.libf.taigamp.state.Session
import de.libf.taigamp.state.postUpdate
import de.libf.taigamp.ui.utils.MutableResultFlow
import de.libf.taigamp.ui.utils.loadOrError
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val tasksRepository: ITasksRepository,
    private val session: Session
) : ViewModel() {

    val creationResult = MutableResultFlow<CommonTask>()

    fun createTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long? = null,
        sprintId: Long? = null,
        statusId: Long? = null,
        swimlaneId: Long? = null
    ) = viewModelScope.launch {
        creationResult.loadOrError(preserveValue = false) {
            tasksRepository.createCommonTask(commonTaskType, title, description, parentId, sprintId, statusId, swimlaneId).also {
                session.taskEdit.postUpdate()
            }
        }
    }
}