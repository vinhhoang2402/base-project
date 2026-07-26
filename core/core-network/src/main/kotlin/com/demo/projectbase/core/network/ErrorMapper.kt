package com.demo.projectbase.core.network

object ErrorMapper {
    fun getMessage(throwable: Throwable): String =
        when (throwable) {
            is NetworkException.NoInternet -> "No internet connection"
            is NetworkException.Unauthorized -> "Session expired, please login again"
            is NetworkException.Forbidden -> "You don't have permission to do this"
            is NetworkException.NotFound -> "Resource not found"
            is NetworkException.ServerError -> "Server error (${throwable.code}), please try again"
            is NetworkException.HttpError -> throwable.message
            else -> throwable.message ?: "Unknown error"
        }
}
