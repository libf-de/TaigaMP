package de.libf.taigamp.ui.screens.main

import androidx.lifecycle.*
import de.libf.taigamp.state.Session
import de.libf.taigamp.state.Settings
import de.libf.taigamp.TaigaApp
import de.libf.taigamp.dagger.AppComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel(private val session: Session, private val settings: Settings) : ViewModel() {
    val isLogged by lazy { session.isLogged }
    val isProjectSelected by lazy { session.isProjectSelected }

    val theme by lazy { settings.themeSetting }
}
