package com.demo.projectbase.core.ui.biometric

/**
 * Result of checking whether biometric authentication can be used on this device.
 */
enum class BiometricAvailability {
    /** Hardware present, at least one biometric enrolled and ready to use. */
    AVAILABLE,

    /** Device has no biometric hardware. */
    NO_HARDWARE,

    /** Hardware present but the user has not enrolled any biometrics. */
    NOT_ENROLLED,

    /** Biometrics are temporarily unavailable (hardware busy, security update, etc.). */
    UNAVAILABLE,
}
