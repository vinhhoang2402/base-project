package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateEmailUseCaseTest {

    private lateinit var validateUsername: ValidateEmailUseCase

    @Before
    fun setup() {
        validateUsername = ValidateEmailUseCase()
    }

    @Test
    fun `returns error when username is blank`() {
        val result = validateUsername("")
        assertTrue(result is ValidationResult.Error)
        assertEquals(R.string.error_email_empty, (result as ValidationResult.Error).resId)
    }

    @Test
    fun `returns error when username is whitespace only`() {
        val result = validateUsername("   ")
        assertTrue(result is ValidationResult.Error)
    }

    @Test
    fun `returns success for plain username`() {
        val result = validateUsername("tmdbuser")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `returns success for username with numbers`() {
        val result = validateUsername("user123")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `returns success for email-style username`() {
        val result = validateUsername("user@example.com")
        assertTrue(result is ValidationResult.Success)
    }
}
