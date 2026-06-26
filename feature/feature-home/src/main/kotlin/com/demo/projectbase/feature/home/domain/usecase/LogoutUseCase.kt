package com.demo.projectbase.feature.home.domain.usecase

import com.demo.projectbase.core.network.SecurePreferencesManager

class LogoutUseCase(
    private val securePrefs: SecurePreferencesManager,
) {
    operator fun invoke() = securePrefs.clearTokens()
}
