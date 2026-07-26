package com.demo.projectbase.feature.auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.demo.projectbase.core.ui.base.BaseViewModel
import com.demo.projectbase.core.ui.biometric.BiometricAuthOutcome
import com.demo.projectbase.core.ui.biometric.BiometricAvailability
import com.demo.projectbase.core.ui.biometric.BiometricAvailabilityChecker
import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult
import com.demo.projectbase.feature.auth.domain.usecase.BiometricLoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.IsBiometricLoginEnabledUseCase
import com.demo.projectbase.feature.auth.domain.usecase.LoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateEmailUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateLoginPasswordUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidateLoginPasswordUseCase,
    private val biometricLoginUseCase: BiometricLoginUseCase,
    private val isBiometricLoginEnabled: IsBiometricLoginEnabledUseCase,
    private val biometricAvailabilityChecker: BiometricAvailabilityChecker,
) : BaseViewModel<LoginContract.Intent, LoginContract.State, LoginContract.Effect>(LoginContract.State()) {
    override fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.UsernameChanged ->
                updateState {
                    copy(username = intent.username, usernameError = null).withSubmitEnabled()
                }

            is LoginContract.Intent.PasswordChanged ->
                updateState {
                    copy(password = intent.password, passwordError = null).withSubmitEnabled()
                }

            LoginContract.Intent.Submit -> submitLogin()
            LoginContract.Intent.ScreenStarted -> refreshBiometricAvailability()
            LoginContract.Intent.BiometricLoginClicked -> onBiometricLoginClicked()
            is LoginContract.Intent.BiometricResult -> onBiometricResult(intent.outcome)
        }
    }

    private fun LoginContract.State.withSubmitEnabled() = copy(isSubmitEnabled = username.isNotBlank() && password.isNotBlank())

    private fun submitLogin() {
        val current = state.value
        val usernameResult = validateEmail(current.username)
        val passwordResult = validatePassword(current.password)

        var hasError = false
        if (usernameResult is ValidationResult.Error) {
            updateState { copy(usernameError = usernameResult.resId) }
            hasError = true
        }
        if (passwordResult is ValidationResult.Error) {
            updateState { copy(passwordError = passwordResult.resId) }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            handleApiResult(
                result = loginUseCase(current.username, current.password),
                onUnauthorized = {
                    updateState { copy(passwordError = R.string.error_wrong_credentials) }
                },
            ) { 
                emitEffect(LoginContract.Effect.NavigateToHome)
                emitEffect(LoginContract.Effect.NavigateToHome)
            }
            updateState { copy(isLoading = false) }
        }
    }

    /** AC1 + AC5: only expose biometrics when hardware is ready and a session exists. */
    private fun refreshBiometricAvailability() {
        val available =
            biometricAvailabilityChecker.check() == BiometricAvailability.AVAILABLE &&
                isBiometricLoginEnabled()
        updateState { copy(isBiometricLoginAvailable = available) }
    }

    private fun onBiometricLoginClicked() {
        if (state.value.isBiometricLoginAvailable) {
            emitEffect(LoginContract.Effect.ShowBiometricPrompt)
        }
    }

    /** AC4: map the prompt result to the appropriate action/error. */
    private fun onBiometricResult(outcome: BiometricAuthOutcome) {
        when (outcome) {
            BiometricAuthOutcome.Success -> performBiometricLogin()
            // User canceled or a single attempt failed: keep the password form (fallback).
            BiometricAuthOutcome.Canceled,
            BiometricAuthOutcome.Failed,
            -> Unit
            BiometricAuthOutcome.Lockout -> {
                updateState { copy(isBiometricLoginAvailable = false) }
                emitEffect(LoginContract.Effect.ShowMessage(R.string.biometric_error_lockout))
            }
            BiometricAuthOutcome.Error ->
                emitEffect(LoginContract.Effect.ShowMessage(R.string.biometric_error_generic))
        }
    }

    private fun performBiometricLogin() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            biometricLoginUseCase().fold(
                onSuccess = { emitEffect(LoginContract.Effect.NavigateToHome) },
                onFailure = { emitEffect(LoginContract.Effect.ShowMessage(R.string.biometric_error_generic)) },
            )
            updateState { copy(isLoading = false) }
        }
    }
}
