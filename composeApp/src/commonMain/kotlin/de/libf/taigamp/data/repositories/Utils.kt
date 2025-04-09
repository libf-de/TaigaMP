package de.libf.taigamp.data.repositories

import de.libf.taigamp.data.api.CommonTaskResponse
import de.libf.taigamp.data.api.SprintResponse
import de.libf.taigamp.domain.entities.*
import de.libf.taigamp.ui.theme.taigaGray
import de.libf.taigamp.ui.utils.toHex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.IO, block)

inline fun <T> handle404(action: () -> List<T>): List<T> = try {
    action()
} catch (e: Exception) {
    // suppress error if page not found (maximum page was reached)
    //e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
    e.printStackTrace()
    emptyList()
}

fun CommonTaskResponse.toCommonTask(commonTaskType: CommonTaskType) = CommonTask(
    id = id,
    createdDate = created_date,
    title = subject,
    ref = ref,
    status = Status(
        id = status,
        name = status_extra_info.name,
        color = status_extra_info.color,
        type = StatusType.Status
    ),
    assignee = assigned_to_extra_info,
    projectInfo = project_extra_info,
    taskType = commonTaskType,
    colors = color?.let { listOf(it) } ?: epics.orEmpty().map { it.color },
    isClosed = is_closed,
    tags = tags.orEmpty().map { Tag(name = it[0]!!, color = it[1].fixNullColor()) },
    blockedNote = blocked_note.takeIf { is_blocked }
)

private val taigaGrayHex by lazy { taigaGray.toHex() }
fun String?.fixNullColor() = this ?: taigaGrayHex // gray, because api returns null instead of gray -_-

fun SprintResponse.toSprint() = Sprint(
    id = id,
    name = name,
    order = order,
    start = estimated_start,
    end = estimated_finish,
    storiesCount = user_stories.size,
    isClosed = closed
)
