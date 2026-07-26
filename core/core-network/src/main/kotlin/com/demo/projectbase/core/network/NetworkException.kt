package com.demo.projectbase.core.network

sealed class NetworkException(message: String) : java.io.IOException(message) {
    data object NoInternet : NetworkException("No internet connection")

    data class Unauthorized(val code: Int = 401) : NetworkException("Unauthorized")

    data class Forbidden(val code: Int = 403) : NetworkException("Forbidden")

    data class NotFound(val code: Int = 404) : NetworkException("Not found")

    data class ServerError(val code: Int) : NetworkException("Server error ($code)")

    data class HttpError(val code: Int, override val message: String) : NetworkException(message)
}
