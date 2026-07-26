package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<User> = authRepository.login(email, password)
}
