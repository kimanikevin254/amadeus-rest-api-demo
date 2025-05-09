package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/search/cities") {
            // Extract parameters from the request
            val countryCode = call.request.queryParameters["countryCode"]
            val keyword = call.request.queryParameters["keyword"]
            val maxParam = call.request.queryParameters["max"]

            // Make sure keyword is provided
            if (keyword.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "keyword is required")
                )
                return@get
            }

            // Parse max param if provided
            val max = maxParam?.toIntOrNull() ?: 5

            // Call service function
            val result = AmadeusService.searchCity(
                keyword = keyword,
                countryCode = countryCode,
                max = max
            )

            call.respond(result)
        }
    }
}
