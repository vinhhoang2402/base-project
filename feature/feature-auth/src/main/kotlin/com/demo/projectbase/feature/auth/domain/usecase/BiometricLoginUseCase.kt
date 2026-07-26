package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class BiometricLoginUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<User> = authRepository.loginWithBiometricToken()
}
