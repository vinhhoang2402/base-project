package com.demo.projectbase.feature.auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.demo.projectbase.core.ui.base.BaseViewModel
import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.ValidationResult
import com.demo.projectbase.feature.auth.domain.usecase.LoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateEmailUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidatePasswordUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
) : BaseViewModel<LoginContract.Intent, LoginContract.State, LoginContract.Effect>(LoginContract.State()) {

    override fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.UsernameChanged -> updateState {
                copy(username = intent.username, usernameError = null).withSubmitEnabled()
            }

            is LoginContract.Intent.PasswordChanged -> updateState {
                copy(password = intent.password, passwordError = null).withSubmitEnabled()
            }

            LoginContract.Intent.Submit -> submitLogin()
        }
    }

    private fun LoginContract.State.withSubmitEnabled() =
        copy(isSubmitEnabled = username.isNotBlank() && password.isNotBlank())

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
            ) { emitEffect(LoginContract.Effect.NavigateToHome) }
            updateState { copy(isLoading = false) }
        }
    }
}
