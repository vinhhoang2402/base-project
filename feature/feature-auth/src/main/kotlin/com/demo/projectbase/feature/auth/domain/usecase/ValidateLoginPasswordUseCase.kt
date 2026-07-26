package com.demo.projectbase.feature.auth.domain.usecase

import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult

/**
 * Validates the password field on the Login screen.
 * Only checks that the field is not blank — strength rules are
 * enforced at registration time, not at login.
 */
class ValidateLoginPasswordUseCase {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) return ValidationResult.Error(R.string.error_password_empty)
        return ValidationResult.Success
    }
}
