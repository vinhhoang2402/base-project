package com.demo.projectbase.feature.auth.domain.repository

import com.demo.projectbase.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): Result<User>

    /**
     * Whether a previously authenticated session is stored securely and can be
     * unlocked with biometrics (i.e. the user has logged in at least once).
     */
    fun isBiometricLoginEnabled(): Boolean

    /**
     * Restores the user session from the securely stored token after a successful
     * biometric authentication. Fails with
     * [com.demo.projectbase.feature.auth.domain.exception.AuthException.NoStoredSession]
     * when there is nothing stored.
     */
    suspend fun loginWithBiometricToken(): Result<User>
}
