package com.demo.projectbase.feature.auth.domain.exception

/**
 * Domain-level authentication errors that are not network failures.
 */
sealed class AuthException(message: String) : Exception(message) {
    /** No securely stored session is available to perform a biometric quick login. */
    data object NoStoredSession : AuthException("No stored session for biometric login")
}
