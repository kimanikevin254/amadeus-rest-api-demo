package com.example

import io.ktor.server.application.*

fun Application.configureAmadeus() {
    val baseUrl = environment.config.property("amadeus.baseUrl").getString()
    val clientId = environment.config.property("amadeus.clientId").getString()
    val clientSecret = environment.config.property("amadeus.clientSecret").getString()
    AmadeusService.initialize(baseUrl, clientId, clientSecret)

    // Shutdown hook to close the HttpClient
    monitor.subscribe(ApplicationStopped) {
        AmadeusService.close()
    }
}