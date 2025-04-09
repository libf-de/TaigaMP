package de.libf.taigamp.ui.screens.main

import androidx.lifecycle.*
import de.libf.taigamp.state.Session
import de.libf.taigamp.state.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel() : ViewModel(), KoinComponent {
    private val session: Session by inject()
    private val settings: Settings by inject()

    val isLogged by lazy { session.isLogged }
    val isProjectSelected by lazy { session.isProjectSelected }

    val theme by lazy { settings.themeSetting }
}
