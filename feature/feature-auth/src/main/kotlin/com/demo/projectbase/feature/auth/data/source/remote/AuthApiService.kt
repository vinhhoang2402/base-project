package com.demo.projectbase.feature.auth.data.source.remote

import com.demo.projectbase.feature.auth.data.source.remote.dto.CreateSessionRequest
import com.demo.projectbase.feature.auth.data.source.remote.dto.LoginRequest
import com.demo.projectbase.feature.auth.data.source.remote.dto.RequestTokenResponse
import com.demo.projectbase.feature.auth.data.source.remote.dto.SessionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @GET("authentication/token/new")
    suspend fun createRequestToken(): RequestTokenResponse

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLogin(
        @Body request: LoginRequest,
    ): RequestTokenResponse

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body request: CreateSessionRequest,
    ): SessionResponse
}
