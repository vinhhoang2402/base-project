package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult

class ValidatePasswordUseCase() {

    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) return ValidationResult.Error(R.string.error_password_empty)
        if (password.length < 6) return ValidationResult.Error(R.string.error_password_too_short)
        return ValidationResult.Success
    }
}
