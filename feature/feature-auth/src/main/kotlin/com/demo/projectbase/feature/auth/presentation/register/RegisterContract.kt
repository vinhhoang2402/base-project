package com.demo.projectbase.feature.auth.presentation.register

object RegisterContract {

    object State

    sealed interface Intent {
        data object OpenTmdbSignup : Intent
        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data class OpenBrowser(val url: String) : Effect
        data object NavigateBack : Effect
    }
}
