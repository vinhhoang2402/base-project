package com.demo.projectbase.feature.auth.presentation.login

import androidx.annotation.StringRes
import com.demo.projectbase.core.ui.biometric.BiometricAuthOutcome

object LoginContract {
    data class State(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        @StringRes val usernameError: Int? = null,
        @StringRes val passwordError: Int? = null,
        val isSubmitEnabled: Boolean = false,
        /**
         * True only when biometrics are ready AND a stored session exists.
         * When false the UI shows the classic password/PIN form (fallback).
         */
        val isBiometricLoginAvailable: Boolean = false,
    )

    sealed interface Intent {
        data class UsernameChanged(val username: String) : Intent

        data class PasswordChanged(val password: String) : Intent

        data object Submit : Intent

        /** Sent when the login screen becomes visible; refreshes biometric availability. */
        data object ScreenStarted : Intent

        /** User tapped the "sign in with biometrics" button. */
        data object BiometricLoginClicked : Intent

        /** Result reported back from the system BiometricPrompt. */
        data class BiometricResult(val outcome: BiometricAuthOutcome) : Intent
    }

    sealed interface Effect {
        data object NavigateToHome : Effect

        /** Ask the view to display the system BiometricPrompt. */
        data object ShowBiometricPrompt : Effect

        /** Show a transient message (biometric lockout / generic biometric failure). */
        data class ShowMessage(
            @StringRes val messageRes: Int,
        ) : Effect
    }
}
