package com.demo.projectbase.core.ui.biometric

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Thin wrapper around [BiometricPrompt] that shows the standard system biometric
 * dialog from a [Fragment] and reports a semantic [BiometricAuthOutcome].
 *
 * Kept out of the ViewModel so the presentation logic stays free of the Android
 * biometric framework and remains unit-testable.
 */
object BiometricPromptPresenter {
    /**
     * Shows the system [BiometricPrompt].
     *
     * @param negativeButtonText label for the fallback button (e.g. "Use password").
     * @param onOutcome invoked on the main thread with the mapped result.
     */
    fun authenticate(
        fragment: Fragment,
        title: CharSequence,
        subtitle: CharSequence,
        negativeButtonText: CharSequence,
        onOutcome: (BiometricAuthOutcome) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(fragment.requireContext())
        val prompt =
            BiometricPrompt(
                fragment,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onOutcome(BiometricAuthOutcome.Success)
                    }

                    override fun onAuthenticationFailed() {
                        onOutcome(BiometricAuthOutcome.Failed)
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        onOutcome(errorCode.toOutcome())
                    }
                },
            )

        val info =
            PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()

        prompt.authenticate(info)
    }

    private fun Int.toOutcome(): BiometricAuthOutcome =
        when (this) {
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
            BiometricPrompt.ERROR_CANCELED,
            -> BiometricAuthOutcome.Canceled

            BiometricPrompt.ERROR_LOCKOUT,
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT,
            -> BiometricAuthOutcome.Lockout

            else -> BiometricAuthOutcome.Error
        }
}
