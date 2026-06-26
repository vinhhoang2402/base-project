package com.demo.projectbase.feature.auth.domain.repository

import com.demo.projectbase.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
}
