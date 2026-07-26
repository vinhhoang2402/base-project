package com.demo.projectbase.feature.auth.data.remote.model

open class BaseResponse(
    open val code: Int = 0,
    open val message: String? = null
)

class LoginResponse(
    val token: String,
    val user: UserDto,
    override val code: Int,
    override val message: String?
) : BaseResponse(code, message)

class UserDto(
    val id: String,
    val email: String,
    val fullName: String
)
