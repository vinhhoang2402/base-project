package com.demo.projectbase.feature.auth.presentation.login

import androidx.annotation.StringRes

object LoginContract {

    data class State(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        @StringRes val usernameError: Int? = null,
        @StringRes val passwordError: Int? = null,
        val isSubmitEnabled: Boolean = false,
    )

    sealed interface Intent {
        data class UsernameChanged(val username: String) : Intent
        data class PasswordChanged(val password: String) : Intent
        data object Submit : Intent
    }

    sealed interface Effect {
        data object NavigateToHome : Effect
    }
}
