package com.example

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    private val json = Json { ignoreUnknownKeys = true }

    fun create(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(json)
            }
        }
    }
}