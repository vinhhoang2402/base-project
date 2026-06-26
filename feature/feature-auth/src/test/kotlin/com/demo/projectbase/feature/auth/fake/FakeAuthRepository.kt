package com.demo.projectbase.feature.auth.fake

import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    private val users = mutableMapOf<String, Pair<String, User>>()
    var shouldReturnError = false
    var errorMessage = "Unexpected error"

    override suspend fun login(email: String, password: String): Result<User> {
        if (shouldReturnError) return Result.failure(Exception(errorMessage))
        val (storedPassword, user) = users[email] ?: return Result.failure(Exception("User not found"))
        return if (storedPassword == password) Result.success(user)
        else Result.failure(Exception("Invalid password"))
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        if (shouldReturnError) return Result.failure(Exception(errorMessage))
        if (users.containsKey(email)) return Result.failure(Exception("Email already registered"))
        val user = User(id = email.hashCode().toString(), email = email, name = name)
        users[email] = password to user
        return Result.success(user)
    }

    fun seedUser(email: String, password: String, name: String = "Test User") {
        val user = User(id = email.hashCode().toString(), email = email, name = name)
        users[email] = password to user
    }
}
