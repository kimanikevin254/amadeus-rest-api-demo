package com.example

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ClientRequestException> { call, cause ->
            val body = cause.response.bodyAsText()

            val json = Json { ignoreUnknownKeys = true } // Configure json for more lenient parsing

            val parsedErrors = try {
                // First try the wrapped errors format
                json.decodeFromString<AmadeusErrorResponse>(body).errors
            } catch (e: Exception) {
                try {
                    // If that fails, try parsing as a single error object
                    // To cover scenarios where the error is not wrapped in top-level errors key
                    listOf(json.decodeFromString<AmadeusError>(body))
                } catch (e2: Exception) {
                    println("Error parsing Amadeus response: ${e::class.simpleName}: ${e.message}")
                    null
                }
            }

            val response = ErrorResponse(
                status = cause.response.status.value,
                message = "Something went wrong",
                errors = parsedErrors
            )

            println("Response to client: $response")
            call.respond(HttpStatusCode.fromValue(response.status), response)
        }

        exception<Throwable> { call, cause ->
            println("Caught unexpected exception: ${cause}")
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    status = 500,
                    message = "Unexpected error: ${cause.message}"
                )
            )
        }
    }
}

@Serializable
data class AmadeusError(
    val code: Int? = null,
    val title: String? = null,
    val detail: String? = null,
    val error_description: String? = null
)

@Serializable
data class AmadeusErrorResponse(
    val errors: List<AmadeusError>
)

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
    val errors: List<AmadeusError>? = null
)