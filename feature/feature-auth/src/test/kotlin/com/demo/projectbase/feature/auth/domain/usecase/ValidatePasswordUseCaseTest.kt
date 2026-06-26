package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidatePasswordUseCaseTest {

    private lateinit var validatePassword: ValidatePasswordUseCase

    @Before
    fun setup() {
        validatePassword = ValidatePasswordUseCase()
    }

    @Test
    fun `returns error when password is blank`() {
        val result = validatePassword("")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_empty, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when password is shorter than 6 characters`() {
        val result = validatePassword("abc")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_too_short, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns success when password is exactly 6 characters`() {
        val result = validatePassword("abcdef")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `returns success when password is longer than 6 characters`() {
        val result = validatePassword("strongPassword123")
        assertTrue(result is ValidationResult.Success)
    }
}
