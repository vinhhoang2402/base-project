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

    // --- Blank / empty ---

    @Test
    fun `returns error when password is blank`() {
        val result = validatePassword("")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_empty, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when password is only whitespace`() {
        val result = validatePassword("        ")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_empty, (result as ValidationResult.Error).resId)
    }

    // --- Length ---

    @Test
    fun `returns error when password is shorter than 8 characters`() {
        val result = validatePassword("Abc1!")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_too_short, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when password is exactly 7 characters`() {
        val result = validatePassword("Abc123!")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_too_short, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when password exceeds 20 characters`() {
        val result = validatePassword("Abcdefgh123456789012!") // 21 chars
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_too_long, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns success when password is exactly 8 characters with all rules met`() {
        val result = validatePassword("Abcde1@!")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `returns success when password is exactly 20 characters with all rules met`() {
        val result = validatePassword("Abcdefghij1234567@#\$")
        assertTrue(result is ValidationResult.Success)
    }

    // --- Uppercase ---

    @Test
    fun `returns error when password has no uppercase letter`() {
        val result = validatePassword("abcde123!")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_no_uppercase, (result as ValidationResult.Error).resId)
    }

    // --- Digit ---

    @Test
    fun `returns error when password has no digit`() {
        val result = validatePassword("Abcdefgh!")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_no_digit, (result as ValidationResult.Error).resId)
    }

    // --- Special character ---

    @Test
    fun `returns error when password has no special character`() {
        val result = validatePassword("Abcdefg1")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_no_special_char, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `accepts each valid special character`() {
        val specialChars = listOf('@', '#', '$', '%', '!')
        specialChars.forEach { char ->
            val result = validatePassword("Abcdef1" + char)
            assertTrue("Expected success for special char '$char'", result is ValidationResult.Success)
        }
    }

    // --- Valid passwords ---

    @Test
    fun `returns success for valid password with all requirements`() {
        val result = validatePassword("Secure1@")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `returns success for valid password 16 chars`() {
        val result = validatePassword("StrongPass123@##")
        assertTrue(result is ValidationResult.Success)
    }

    // --- Rule order: blank checked first ---

    @Test
    fun `blank password returns empty error not too short`() {
        val result = validatePassword("   ")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_password_empty, (result as ValidationResult.Error).resId)
    }
}
