package de.libf.taigamp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import de.libf.taigamp.data.api.RefreshTokenRequest
import de.libf.taigamp.data.api.RefreshTokenResponse
import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.getVersionName
import de.libf.taigamp.state.Session
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

val platformModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { androidContext().filesDir.resolve(dataStoreFileName).absolutePath.toPath() }
        )
    }

    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(UserAgent) {
                agent = "TaigaMP/${getVersionName()}"
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from a local storage and return them as the 'BearerTokens' instance
                        BearerTokens(get<Session>().token.last(), get<Session>().refreshToken.last())
                    }

                    refreshTokens {
                        val response: HttpResponse = client.post {
                            url("${TaigaApi.REFRESH_ENDPOINT}")
                            contentType(ContentType.Application.Json)
                            setBody(RefreshTokenRequest(oldTokens?.refreshToken ?: ""))
                        }
                        val parsedResponse =
                            Json.decodeFromString<RefreshTokenResponse>(response.bodyAsText())

                        get<Session>().changeAuthCredentials(parsedResponse.auth_token, parsedResponse.refresh)

                        BearerTokens(parsedResponse.auth_token, parsedResponse.refresh)
                    }
                }
            }

            defaultRequest {
                url(runBlocking { getApiUrl(get<Session>()) })
            }
        }
    }
}