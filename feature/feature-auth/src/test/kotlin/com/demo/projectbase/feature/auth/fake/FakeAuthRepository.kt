package com.demo.projectbase.feature.auth.fake

import com.demo.projectbase.feature.auth.domain.exception.AuthException
import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    private val users = mutableMapOf<String, Pair<String, User>>()
    var shouldReturnError = false
    var errorMessage = "Unexpected error"

    /** Set to a non-null user to simulate a stored session unlockable by biometrics. */
    var storedSession: User? = null

    override suspend fun login(
        email: String,
        password: String,
    ): Result<User> {
        if (shouldReturnError) return Result.failure(Exception(errorMessage))
        val (storedPassword, user) = users[email] ?: return Result.failure(Exception("User not found"))
        return if (storedPassword == password) {
            storedSession = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid password"))
        }
    }

    override fun isBiometricLoginEnabled(): Boolean = storedSession != null

    override suspend fun loginWithBiometricToken(): Result<User> =
        storedSession?.let { Result.success(it) } ?: Result.failure(AuthException.NoStoredSession)

    fun seedUser(
        email: String,
        password: String,
        name: String = "Test User",
    ) {
        val user = User(id = email.hashCode().toString(), email = email, name = name)
        users[email] = password to user
    }
}
