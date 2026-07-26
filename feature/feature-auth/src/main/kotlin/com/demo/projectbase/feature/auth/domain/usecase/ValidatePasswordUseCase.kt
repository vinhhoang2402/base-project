package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult

class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) return ValidationResult.Error(R.string.error_password_empty)
        if (password.length < MIN_LENGTH) return ValidationResult.Error(R.string.error_password_too_short)
        if (password.length > MAX_LENGTH) return ValidationResult.Error(R.string.error_password_too_long)
        if (!password.any { it.isUpperCase() }) return ValidationResult.Error(R.string.error_password_no_uppercase)
        if (!password.any { it.isDigit() }) return ValidationResult.Error(R.string.error_password_no_digit)
        if (!password.any { it in SPECIAL_CHARS }) return ValidationResult.Error(R.string.error_password_no_special_char)
        return ValidationResult.Success
    }

    companion object {
        const val MIN_LENGTH = 8
        const val MAX_LENGTH = 20
        const val SPECIAL_CHARS = "@#\$%!"
    }
}
