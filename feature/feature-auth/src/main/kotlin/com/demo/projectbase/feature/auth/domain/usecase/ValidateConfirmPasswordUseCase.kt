package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult

class ValidateConfirmPasswordUseCase {
    operator fun invoke(
        password: String,
        confirmPassword: String,
    ): ValidationResult {
        if (confirmPassword.isBlank()) return ValidationResult.Error(R.string.error_password_empty)
        if (password != confirmPassword) return ValidationResult.Error(R.string.error_passwords_mismatch)
        return ValidationResult.Success
    }
}
