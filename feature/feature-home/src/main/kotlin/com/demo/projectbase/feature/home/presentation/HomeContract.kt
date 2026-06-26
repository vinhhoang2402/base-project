package com.demo.projectbase.feature.home.presentation

object HomeContract {

    data class State(val isLoggedIn: Boolean = false)

    sealed interface Intent {
        data object Logout : Intent
        data object Login : Intent
    }

    sealed interface Effect {
        data object NavigateToLogin : Effect
    }
}
