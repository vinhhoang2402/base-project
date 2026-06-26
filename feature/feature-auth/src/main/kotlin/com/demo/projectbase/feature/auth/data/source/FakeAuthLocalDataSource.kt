package com.demo.projectbase.feature.auth.data.source

import com.demo.projectbase.feature.auth.domain.model.User
import kotlinx.coroutines.delay

class FakeAuthLocalDataSource {

    private val users = mutableMapOf<String, Pair<String, User>>()

    suspend fun login(email: String, password: String): Result<User> {
        delay(600)
        val (storedPassword, user) = users[email]
            ?: return Result.failure(Exception("No account found with this email"))
        return if (storedPassword == password) Result.success(user)
        else Result.failure(Exception("Incorrect password"))
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        delay(600)
        if (users.containsKey(email)) return Result.failure(Exception("Email is already registered"))
        val user = User(
            id = email.hashCode().toString(),
            email = email,
            name = name,
        )
        users[email] = password to user
        return Result.success(user)
    }
}
