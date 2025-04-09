package de.libf.taigamp.state

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Settings(private val prefs: DataStore<Preferences>) {
    val themeSetting: Flow<ThemeSetting> = prefs.data.map { ThemeSetting.entries[it[THEME] ?: 0] }

    suspend fun changeThemeSetting(value: ThemeSetting) {
        prefs.edit { it[THEME] = value.ordinal }
    }

    companion object {
        private const val PREFERENCES_NAME = "settings"
        private val THEME = intPreferencesKey("theme")
    }
}

enum class ThemeSetting {
    System,
    Light,
    Dark
}
