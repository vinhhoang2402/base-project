package com.demo.projectbase.core.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(
    private val apiKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().withBearerToken(apiKey))

    private fun Request.withBearerToken(token: String): Request =
        newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
}
