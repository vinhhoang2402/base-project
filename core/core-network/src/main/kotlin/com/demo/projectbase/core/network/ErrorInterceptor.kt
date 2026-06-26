package com.demo.projectbase.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = try {
            chain.proceed(chain.request())
        } catch (e: IOException) {
            throw NetworkException.NoInternet
        }

        if (response.isSuccessful) return response

        val errorBody = response.body?.string().orEmpty()
        response.close()

        throw when (response.code) {
            401 -> NetworkException.Unauthorized()
            403 -> NetworkException.Forbidden()
            404 -> NetworkException.NotFound()
            in 500..599 -> NetworkException.ServerError(response.code)
            else -> NetworkException.HttpError(response.code, errorBody)
        }
    }
}
