package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateConfirmPasswordUseCaseTest {
    private lateinit var validateConfirmPassword: ValidateConfirmPasswordUseCase

    @Before
    fun setup() {
        validateConfirmPassword = ValidateConfirmPasswordUseCase()
    }

    @Test
    fun `returns error when confirm password is blank`() {
        val result = validateConfirmPassword("Secure1@", "")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_empty, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when passwords do not match`() {
        val result = validateConfirmPassword("Secure1@", "Different1!")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_passwords_mismatch, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when confirm differs only by case`() {
        val result = validateConfirmPassword("Secure1@", "secure1@")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_passwords_mismatch, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns success when passwords match`() {
        val result = validateConfirmPassword("Secure1@", "Secure1@")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `returns success when both passwords are identical complex strings`() {
        val password = "StrongP@55word!"
        val result = validateConfirmPassword(password, password)
        assertTrue(result is ValidationResult.Success)
    }
}
