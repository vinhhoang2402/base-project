package com.demo.projectbase.core.ui.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG

/**
 * Checks whether strong biometric authentication is usable on this device.
 *
 * Abstracted behind an interface so ViewModels can depend on it and tests can
 * supply a fake without pulling in the Android biometric framework.
 */
fun interface BiometricAvailabilityChecker {
    fun check(): BiometricAvailability
}

/**
 * Default [BiometricAvailabilityChecker] backed by [BiometricManager], requiring
 * [BIOMETRIC_STRONG] (Class 3) authenticators.
 */
class DefaultBiometricAvailabilityChecker(
    private val context: Context,
) : BiometricAvailabilityChecker {
    override fun check(): BiometricAvailability =
        when (BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NOT_ENROLLED
            else -> BiometricAvailability.UNAVAILABLE
        }
}
