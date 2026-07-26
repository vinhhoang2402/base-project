package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class IsBiometricLoginEnabledUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Boolean = authRepository.isBiometricLoginEnabled()
}
