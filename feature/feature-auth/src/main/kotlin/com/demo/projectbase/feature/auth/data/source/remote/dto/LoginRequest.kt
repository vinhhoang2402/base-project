package com.demo.projectbase.feature.auth.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("request_token") val requestToken: String,
)
