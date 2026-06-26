package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult

class ValidateEmailUseCase() {

    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) return ValidationResult.Error(R.string.error_email_empty)
        return ValidationResult.Success
    }
}
