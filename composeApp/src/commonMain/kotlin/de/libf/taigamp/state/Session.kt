package de.libf.taigamp.state

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import de.libf.taigamp.domain.entities.FiltersData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Global app state
 */
class Session(private val prefs: DataStore<Preferences>) {
    //private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    val refreshToken = prefs.data.map { it[REFRESH_TOKEN_KEY] ?: "" }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )
    val token = prefs.data.map { it[TOKEN_KEY] ?: "" }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )

    //private val _refreshToken = MutableStateFlow(sharedPreferences.getString(REFRESH_TOKEN_KEY, "").orEmpty())
    //private val _token = MutableStateFlow(sharedPreferences.getString(TOKEN_KEY, "").orEmpty())

    //val refreshToken: StateFlow<String> = _refreshToken
    //val token: StateFlow<String> = _token

    suspend fun changeAuthCredentials(token: String, refreshToken: String) {
        prefs.edit {
            it[TOKEN_KEY] = token
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
        //_token.value = token
        //_refreshToken.value = refreshToken
    }

    //private val _server = MutableStateFlow(sharedPreferences.getString(SERVER_KEY, "").orEmpty())
    //val server: StateFlow<String> = _server
    val server = prefs.data.map { it[SERVER_KEY] ?: "" }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )
    suspend fun changeServer(value: String) {
        prefs.edit { it[SERVER_KEY] = value; };
    }

    //private val _currentUserId = MutableStateFlow(sharedPreferences.getLong(USER_ID_KEY, -1))
    //val currentUserId: StateFlow<Long> = _currentUserId
    val currentUserId = prefs.data.map { it[USER_ID_KEY] ?: -1 }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = -1
    )
    suspend fun changeCurrentUserId(value: Long) {
        prefs.edit { it[USER_ID_KEY] = value; };
    }

    //private val _currentProjectId = MutableStateFlow(sharedPreferences.getLong(PROJECT_ID_KEY, -1))
    //private val _currentProjectName = MutableStateFlow(sharedPreferences.getString(PROJECT_NAME_KEY, "").orEmpty())

    //val currentProjectId: StateFlow<Long> = _currentProjectId
    //val currentProjectName: StateFlow<String> = _currentProjectName
    val currentProjectId = prefs.data.map { it[PROJECT_ID_KEY] ?: -1 }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = -1
    )
    val currentProjectName = prefs.data.map { it[PROJECT_NAME_KEY] ?: "" }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )

    suspend fun changeCurrentProject(id: Long, name: String) {
        prefs.edit {
            it[PROJECT_ID_KEY] = id;
            it[PROJECT_NAME_KEY] = name;
        }

        resetFilters()
    }

    private fun checkLogged(token: String, refresh: String) = listOf(token, refresh).all { it.isNotEmpty() }
    val isLogged = combine(token, refreshToken, ::checkLogged)
        .stateIn(scope, SharingStarted.Eagerly, initialValue = null)

    private fun checkProjectSelected(id: Long) = id >= 0
    val isProjectSelected = currentProjectId.map(::checkProjectSelected)
        .stateIn(scope, SharingStarted.Eagerly, initialValue = checkProjectSelected(currentProjectId.value))


    // Filters
//    private fun getFiltersOrEmpty(key: String) = sharedPreferences.getString(key, null)?.takeIf { it.isNotBlank() }?.let { filtersJsonAdapter.fromJson(it) } ?: FiltersData()
//    private val _scrumFilters = MutableStateFlow(getFiltersOrEmpty(FILTERS_SCRUM))
    val scrumFilters = prefs.data
        .map { it[FILTERS_SCRUM]?.let { s -> Json.decodeFromString(s) } ?: FiltersData() }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = FiltersData()
        )

    suspend fun changeScrumFilters(filters: FiltersData) {
        prefs.edit {
            it[FILTERS_SCRUM] = Json.encodeToString(filters)
        }
    }

//    private val _epicsFilters = MutableStateFlow(getFiltersOrEmpty(FILTERS_EPICS))
//    val epicsFilters: StateFlow<FiltersData> = _epicsFilters
    val epicsFilters = prefs.data
        .map { it[FILTERS_EPICS]?.let { s -> Json.decodeFromString(s) } ?: FiltersData() }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = FiltersData()
        )
    suspend fun changeEpicsFilters(filters: FiltersData) {
        prefs.edit {
            it[FILTERS_EPICS] = Json.encodeToString(filters);
        }
    }

    val issuesFilters = prefs.data
        .map { it[FILTERS_ISSUES]?.let { s -> Json.decodeFromString(s) } ?: FiltersData() }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = FiltersData()
        )

    suspend fun changeIssuesFilters(filters: FiltersData) {
        prefs.edit {
            it[FILTERS_ISSUES] = Json.encodeToString(filters)
        }
    }

    private suspend fun resetFilters() {
        changeScrumFilters(FiltersData())
        changeEpicsFilters(FiltersData())
        changeIssuesFilters(FiltersData())
    }


    suspend fun reset() {
        changeAuthCredentials("", "")
        changeServer("")
        changeCurrentUserId(-1)
        changeCurrentProject(-1, "")

        resetFilters()
    }

    companion object {
        private const val PREFERENCES_NAME = "session"
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val REFRESH_TOKEN_KEY =  stringPreferencesKey("refresh_token")
        private val SERVER_KEY = stringPreferencesKey("server")
        private val PROJECT_NAME_KEY = stringPreferencesKey("project_name")
        private val PROJECT_ID_KEY = longPreferencesKey("project_id")
        private val USER_ID_KEY = longPreferencesKey("user_id")

        private val FILTERS_SCRUM = stringPreferencesKey("filters_scrum")
        private val FILTERS_EPICS = stringPreferencesKey("filters_epics")
        private val FILTERS_ISSUES = stringPreferencesKey("filters_issues")
    }

    // Events (no data, just dispatch update to subscribers)

    val taskEdit = EventFlow() // some task was edited
    val sprintEdit = EventFlow() // sprint was edited
}

/**
 * An empty class which describes basic event without any data (for the sake of update only)
 */
class Event
@Suppress("FunctionName")
fun EventFlow() = MutableSharedFlow<Event>()

suspend fun MutableSharedFlow<Event>.postUpdate() = emit(Event())
fun MutableSharedFlow<Event>.tryPostUpdate() = tryEmit(Event())

fun CoroutineScope.subscribeToAll(vararg flows: Flow<*>, action: () -> Unit) {
    flows.forEach {
        launch {
            it.collect { action() }
        }
    }
}
