package com.demo.projectbase.feature.auth.presentation.register

import androidx.annotation.StringRes

object RegisterContract {
    data class State(
        val password: String = "",
        val confirmPassword: String = "",
        @StringRes val passwordError: Int? = null,
        @StringRes val confirmPasswordError: Int? = null,
        val isRegisterEnabled: Boolean = false,
    )

    sealed interface Intent {
        data class PasswordChanged(val password: String) : Intent

        data class ConfirmPasswordChanged(val confirmPassword: String) : Intent

        data object OpenTmdbSignup : Intent

        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data class OpenBrowser(val url: String) : Effect

        data object NavigateBack : Effect
    }
}
