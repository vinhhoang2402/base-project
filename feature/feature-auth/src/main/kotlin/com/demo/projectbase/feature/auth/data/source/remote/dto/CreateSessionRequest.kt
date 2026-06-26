package com.demo.projectbase.feature.auth.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    @SerialName("request_token") val requestToken: String,
)

@Serializable
data class SessionResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("session_id") val sessionId: String,
)
