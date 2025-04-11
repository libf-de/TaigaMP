package de.libf.taigamp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import de.libf.taigamp.data.buildTaigaHttpClient
import de.libf.taigamp.state.Session
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import okio.Path.Companion.toPath
import org.koin.dsl.module

val platformModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { ("../../" + dataStoreFileName).toPath() }
        )
    }

    single<HttpClient> {
        buildTaigaHttpClient(CIO, get<Session>())
    }
}