package de.libf.taigamp.data

import de.libf.taigamp.data.api.RefreshTokenRequest
import de.libf.taigamp.data.api.RefreshTokenResponse
import de.libf.taigamp.data.api.TaigaApi
import de.libf.taigamp.getVersionName
import de.libf.taigamp.state.Session
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun getApiUrl(session: Session) = // for compatibility with older app versions
    if (!session.server.value.run { startsWith("https://") || startsWith("http://") }) {
        "https://"
    } else {
        ""
    } + "${session.server.value}/${TaigaApi.API_PREFIX}/"

fun <T : HttpClientEngineConfig> buildTaigaHttpClient(engineFactory: HttpClientEngineFactory<T>, session: Session): HttpClient {
    return HttpClient(engineFactory) {
        install(Logging) {
            logger = object: Logger {
                override fun log(message: String) {
                    Napier.v("HTTP Client", null, message)
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(
                Json {
                    explicitNulls = false
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
                    BearerTokens(session.token.value, session.refreshToken.value)
                }

                refreshTokens {
                    val response: HttpResponse = client.post {
                        url("${TaigaApi.REFRESH_ENDPOINT}")
                        contentType(ContentType.Application.Json)
                        setBody(RefreshTokenRequest(oldTokens?.refreshToken ?: ""))
                    }
                    val parsedResponse =
                        Json.decodeFromString<RefreshTokenResponse>(response.bodyAsText())

                    session.changeAuthCredentials(parsedResponse.auth_token, parsedResponse.refresh)

                    BearerTokens(parsedResponse.auth_token, parsedResponse.refresh)
                }

                sendWithoutRequest { request ->
                    // Don't send auth headers to auth endpoints
                    !request.url.encodedPath.let { it.contains("/auth") || it.contains(TaigaApi.REFRESH_ENDPOINT) }
                }
            }
        }

        defaultRequest {
//            url("https://api.taiga.io/api/v1/")
//                url("http://localhost:9999/api/v1/")
            url(getApiUrl(session))
        }
    }
}