package com.github.hofmmaxi.exaroton.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

internal object Ktor {
    internal lateinit var token: String
    internal var client = HttpClient(CIO) {
        expectSuccess = true

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.exaroton.com"
                path("v1/")
            }
            header(HttpHeaders.Authorization, "Bearer $token")
            userAgent("com.github.hofmmaxi.exaroton@1.0.0")
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = false
            })
        }

        install(Resources)

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingInterval = 3_000
        }

    }
}