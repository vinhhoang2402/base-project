package com.demo.projectbase.feature.auth.data.repository

import com.demo.projectbase.core.network.SecurePreferencesManager
import com.demo.projectbase.feature.auth.data.source.remote.AuthRemoteDataSource
import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val securePrefs: SecurePreferencesManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> =
        remoteDataSource.login(email, password).map { sessionId ->
            securePrefs.saveTokens(sessionId, email)
            User(id = sessionId, email = email, name = email)
        }
}
