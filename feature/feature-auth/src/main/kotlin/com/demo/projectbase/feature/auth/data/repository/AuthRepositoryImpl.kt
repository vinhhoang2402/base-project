package com.demo.projectbase.feature.auth.data.repository

import com.demo.projectbase.core.network.SecurePreferencesManager
import com.demo.projectbase.feature.auth.data.source.remote.AuthRemoteDataSource
import com.demo.projectbase.feature.auth.domain.exception.AuthException
import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val securePrefs: SecurePreferencesManager,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<User> =
        remoteDataSource.login(email, password).map { sessionId ->
            // Session token + identity are persisted in EncryptedSharedPreferences,
            // enabling later biometric quick login.
            securePrefs.saveTokens(sessionId, email)
            User(id = sessionId, email = email, name = email)
        }

    override fun isBiometricLoginEnabled(): Boolean = securePrefs.getAccessToken() != null

    override suspend fun loginWithBiometricToken(): Result<User> {
        val token =
            securePrefs.getAccessToken()
                ?: return Result.failure(AuthException.NoStoredSession)
        val email = securePrefs.getRefreshToken().orEmpty()
        return Result.success(User(id = token, email = email, name = email))
    }
}
