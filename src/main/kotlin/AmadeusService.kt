package com.example

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.formUrlEncode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

object AmadeusService {
    private var baseUrl: String? = null
    private var clientId: String? = null
    private var clientSecret: String? = null
    private var cachedToken: String? = null
    private var tokenExpirationTime: Long = 0
    private val mutex = Mutex() // For thread safety

    // Lazy property to only create the client when needed
    private val httpClient by lazy { HttpClientFactory.create() }

    fun initialize(baseUrl: String, clientId: String, clientSecret: String) {
        this.baseUrl = baseUrl
        this.clientId = clientId
        this.clientSecret = clientSecret
    }

    // Close HttpClient
    fun close() {
        httpClient.close()
    }

    // Generate an access token
    suspend fun getAccessToken(): String {
        // Check for a valid cached token
        mutex.withLock {
            val currentTime = Instant.now().epochSecond
            if (cachedToken != null && currentTime < tokenExpirationTime - 60) {
                println("Token is still valid. No need to get a new one.")
                return cachedToken!!
            }
        }

        // Token expired or not available. Get a new one
        println("Obtaining a new token")
        val response: HttpResponse = httpClient.post("$baseUrl/v1/security/oauth2/token") {
            headers {
                append("Content-Type", "application/x-www-form-urlencoded")
            }
            setBody(
                listOf(
                    "grant_type" to "client_credentials",
                    "client_id" to clientId,
                    "client_secret" to clientSecret
                ).formUrlEncode()
            )
        }

        val tokenResponse: AccessTokenResponse = response.body()
        cachedToken = tokenResponse.accessToken

        // Calculate expiration time (current time + expires in)
        tokenExpirationTime = Instant.now().epochSecond + tokenResponse.expiresIn

        return cachedToken!!
    }

    // Search for cities using the provided input
    suspend fun searchCity(
        keyword: String,
        countryCode: String? = null,
        max: Int? = 5,
    ): CitySearchResponse {
        // Input validation
        require(keyword.isNotBlank()) { "keyword must not be blank" }
        require(max in 1..10) { "Max results must be between 1 and 10" }

        // Retrieve an access token to authenticate the request
        val accessToken = getAccessToken()

        // Make the API call
        val response = httpClient.get("$baseUrl/v1/reference-data/locations/cities") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            url {
                parameters.append("keyword", keyword)
                parameters.append("max", max.toString())

                // Only add the countryCode if it's not null and not blank
                countryCode?.takeIf { it.isNotBlank() }?.let {
                    parameters.append("countryCode", it)
                }
            }
        }

        return response.body()
    }
}

@Serializable
data class AccessTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
)

@Serializable
data class CitySearchResponse(
    val data: List<Location>,
)

@Serializable
data class Location(
    val type: String,
    val subType: String,
    val name: String,
    val iataCode: String? = null,
    val address: Address,
    val geoCode: GeoCode,
)

@Serializable
data class Address(
    val postalCode: String? = null,
    val countryCode: String,
    val stateCode: String? = null,
)

@Serializable
data class GeoCode(
    val latitude: Double,
    val longitude: Double,
)