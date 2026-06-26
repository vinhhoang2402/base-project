package com.demo.projectbase.feature.auth.data.source.remote

import com.demo.projectbase.core.network.safeSuspend
import com.demo.projectbase.feature.auth.data.source.remote.dto.CreateSessionRequest
import com.demo.projectbase.feature.auth.data.source.remote.dto.LoginRequest

class AuthRemoteDataSource(
    private val apiService: AuthApiService,
) {
    suspend fun login(email: String, password: String): Result<String> = safeSuspend {
        val tokenResponse = apiService.createRequestToken()
        val validated = apiService.validateWithLogin(
            LoginRequest(
                username = email,
                password = password,
                requestToken = tokenResponse.requestToken,
            )
        )
        apiService.createSession(CreateSessionRequest(validated.requestToken)).sessionId
    }
}
