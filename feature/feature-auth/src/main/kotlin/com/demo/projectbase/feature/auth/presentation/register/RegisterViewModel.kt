package com.demo.projectbase.feature.auth.presentation.register

import com.demo.projectbase.core.ui.base.BaseViewModel
import com.demo.projectbase.feature.auth.domain.model.ValidationResult
import com.demo.projectbase.feature.auth.domain.usecase.ValidateConfirmPasswordUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidatePasswordUseCase

class RegisterViewModel(
    private val validatePassword: ValidatePasswordUseCase,
    private val validateConfirmPassword: ValidateConfirmPasswordUseCase,
) : BaseViewModel<RegisterContract.Intent, RegisterContract.State, RegisterContract.Effect>(RegisterContract.State()) {
    override fun handleIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.PasswordChanged -> onPasswordChanged(intent.password)
            is RegisterContract.Intent.ConfirmPasswordChanged -> onConfirmPasswordChanged(intent.confirmPassword)
            RegisterContract.Intent.OpenTmdbSignup ->
                emitEffect(RegisterContract.Effect.ShowTermsDialog)
            RegisterContract.Intent.NavigateBack ->
                emitEffect(RegisterContract.Effect.NavigateBack)
        }
    }

    private fun onPasswordChanged(password: String) {
        val passwordResult = validatePassword(password)
        val confirmResult = validateConfirmPassword(password, state.value.confirmPassword)
        updateState {
            copy(
                password = password,
                passwordError = (passwordResult as? ValidationResult.Error)?.resId,
            ).withRegisterEnabled(passwordResult, confirmResult)
        }
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        val passwordResult = validatePassword(state.value.password)
        val confirmResult = validateConfirmPassword(state.value.password, confirmPassword)
        updateState {
            copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = (confirmResult as? ValidationResult.Error)?.resId,
            ).withRegisterEnabled(passwordResult, confirmResult)
        }
    }

    private fun RegisterContract.State.withRegisterEnabled(
        passwordResult: ValidationResult,
        confirmResult: ValidationResult,
    ): RegisterContract.State =
        copy(
            isRegisterEnabled =
                passwordResult is ValidationResult.Success &&
                    confirmResult is ValidationResult.Success,
        )
}
