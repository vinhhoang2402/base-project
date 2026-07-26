package com.demo.projectbase.core.ui.biometric

/**
 * Semantic outcome of a [BiometricPromptPresenter] authentication attempt.
 *
 * Raw [androidx.biometric.BiometricPrompt] error codes are mapped here so that
 * consumers (ViewModels/tests) never need to depend on the Android biometric API.
 */
sealed interface BiometricAuthOutcome {
    /** User was authenticated successfully. */
    data object Success : BiometricAuthOutcome

    /** A biometric was presented but not recognised; the system prompt stays open. */
    data object Failed : BiometricAuthOutcome

    /** User dismissed the prompt or tapped the negative ("use password") button. */
    data object Canceled : BiometricAuthOutcome

    /** Too many failed attempts — biometrics are locked out for now. */
    data object Lockout : BiometricAuthOutcome

    /** Any other unrecoverable error. */
    data object Error : BiometricAuthOutcome
}
