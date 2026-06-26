package com.demo.projectbase.feature.auth.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("token") val token: String,
    @SerialName("user") val user: UserDto,
)

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
)
