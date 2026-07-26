package com.demo.projectbase.feature.auth.domain.model

import androidx.annotation.StringRes

sealed class ValidationResult {
    data object Success : ValidationResult()

    data class Error(
        @StringRes val resId: Int,
    ) : ValidationResult()
}
