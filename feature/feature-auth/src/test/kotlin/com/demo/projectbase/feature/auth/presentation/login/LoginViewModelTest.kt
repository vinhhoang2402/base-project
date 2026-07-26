package com.demo.projectbase.feature.auth.presentation.login

import app.cash.turbine.test
import com.demo.projectbase.core.network.NetworkException
import com.demo.projectbase.core.testing.MainDispatcherRule
import com.demo.projectbase.core.ui.base.BaseEffect
import com.demo.projectbase.core.ui.biometric.BiometricAuthOutcome
import com.demo.projectbase.core.ui.biometric.BiometricAvailability
import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.exception.AuthException
import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository
import com.demo.projectbase.feature.auth.domain.usecase.BiometricLoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.IsBiometricLoginEnabledUseCase
import com.demo.projectbase.feature.auth.domain.usecase.LoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateEmailUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateLoginPasswordUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()
    private var availability = BiometricAvailability.AVAILABLE
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel =
            LoginViewModel(
                loginUseCase = LoginUseCase(authRepository),
                validateEmail = ValidateEmailUseCase(),
                validatePassword = ValidateLoginPasswordUseCase(),
                biometricLoginUseCase = BiometricLoginUseCase(authRepository),
                isBiometricLoginEnabled = IsBiometricLoginEnabledUseCase(authRepository),
                biometricAvailabilityChecker = { availability },
            )
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.state.value
        assertEquals("", state.username)
        assertEquals("", state.password)
        assertTrue(!state.isLoading)
        assertNull(state.usernameError)
        assertNull(state.passwordError)
        assertFalse(state.isBiometricLoginAvailable)
    }

    @Test
    fun `updating username clears username error`() {
        viewModel.handleIntent(LoginContract.Intent.Submit)
        viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
        assertNull(viewModel.state.value.usernameError)
    }

    @Test
    fun `submit with blank username sets username error`() {
        viewModel.handleIntent(LoginContract.Intent.Submit)
        assertNotNull(viewModel.state.value.usernameError)
    }

    @Test
    fun `submit with blank password sets password error`() {
        viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
        viewModel.handleIntent(LoginContract.Intent.Submit)
        assertNotNull(viewModel.state.value.passwordError)
    }

    @Test
    fun `successful login emits NavigateToHome effect`() =
        runTest {
            coEvery { authRepository.login(any(), any()) } returns
                Result.success(
                    User("1", "tmdbuser", "Test"),
                )

            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
                viewModel.handleIntent(LoginContract.Intent.PasswordChanged("password123"))
                viewModel.handleIntent(LoginContract.Intent.Submit)

                assertEquals(LoginContract.Effect.NavigateToHome, awaitItem())
            }
        }

    @Test
    fun `login with wrong credentials sets passwordError`() =
        runTest {
            coEvery { authRepository.login(any(), any()) } returns
                Result.failure(NetworkException.Unauthorized())

            viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
            viewModel.handleIntent(LoginContract.Intent.PasswordChanged("wrongpass"))
            viewModel.handleIntent(LoginContract.Intent.Submit)

            assertEquals(R.string.error_wrong_credentials, viewModel.state.value.passwordError)
        }

    @Test
    fun `generic error emits baseEffect ShowError`() =
        runTest {
            coEvery { authRepository.login(any(), any()) } returns
                Result.failure(Exception("Server error"))

            viewModel.baseEffect.test {
                viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
                viewModel.handleIntent(LoginContract.Intent.PasswordChanged("password123"))
                viewModel.handleIntent(LoginContract.Intent.Submit)

                assertTrue(awaitItem() is BaseEffect.ShowError)
            }
        }

    @Test
    fun `isLoading is false after login completes`() =
        runTest {
            coEvery { authRepository.login(any(), any()) } returns
                Result.success(
                    User("1", "tmdbuser", "Test"),
                )

            viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
            viewModel.handleIntent(LoginContract.Intent.PasswordChanged("password123"))
            viewModel.handleIntent(LoginContract.Intent.Submit)

            assertTrue(!viewModel.state.value.isLoading)
        }

    // --- Biometric login (BASE-789) ---

    @Test
    fun `ScreenStarted enables biometrics when hardware ready and session stored`() {
        availability = BiometricAvailability.AVAILABLE
        every { authRepository.isBiometricLoginEnabled() } returns true

        viewModel.handleIntent(LoginContract.Intent.ScreenStarted)

        assertTrue(viewModel.state.value.isBiometricLoginAvailable)
    }

    @Test
    fun `ScreenStarted keeps biometrics disabled when no stored session`() {
        availability = BiometricAvailability.AVAILABLE
        every { authRepository.isBiometricLoginEnabled() } returns false

        viewModel.handleIntent(LoginContract.Intent.ScreenStarted)

        assertFalse(viewModel.state.value.isBiometricLoginAvailable)
    }

    @Test
    fun `ScreenStarted keeps biometrics disabled when hardware unavailable`() {
        availability = BiometricAvailability.NO_HARDWARE
        every { authRepository.isBiometricLoginEnabled() } returns true

        viewModel.handleIntent(LoginContract.Intent.ScreenStarted)

        assertFalse(viewModel.state.value.isBiometricLoginAvailable)
    }

    @Test
    fun `BiometricLoginClicked shows prompt when available`() =
        runTest {
            availability = BiometricAvailability.AVAILABLE
            every { authRepository.isBiometricLoginEnabled() } returns true
            viewModel.handleIntent(LoginContract.Intent.ScreenStarted)

            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.BiometricLoginClicked)
                assertEquals(LoginContract.Effect.ShowBiometricPrompt, awaitItem())
            }
        }

    @Test
    fun `BiometricLoginClicked does nothing when unavailable`() =
        runTest {
            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.BiometricLoginClicked)
                expectNoEvents()
            }
        }

    @Test
    fun `biometric success emits NavigateToHome`() =
        runTest {
            coEvery { authRepository.loginWithBiometricToken() } returns
                Result.success(User("token", "tmdbuser", "Test"))

            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.BiometricResult(BiometricAuthOutcome.Success))
                assertEquals(LoginContract.Effect.NavigateToHome, awaitItem())
            }
        }

    @Test
    fun `biometric success with no stored session shows generic message`() =
        runTest {
            coEvery { authRepository.loginWithBiometricToken() } returns
                Result.failure(AuthException.NoStoredSession)

            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.BiometricResult(BiometricAuthOutcome.Success))
                assertEquals(
                    LoginContract.Effect.ShowMessage(R.string.biometric_error_generic),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `biometric lockout disables biometrics and shows message`() =
        runTest {
            availability = BiometricAvailability.AVAILABLE
            every { authRepository.isBiometricLoginEnabled() } returns true
            viewModel.handleIntent(LoginContract.Intent.ScreenStarted)

            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.BiometricResult(BiometricAuthOutcome.Lockout))
                assertEquals(
                    LoginContract.Effect.ShowMessage(R.string.biometric_error_lockout),
                    awaitItem(),
                )
            }
            assertFalse(viewModel.state.value.isBiometricLoginAvailable)
        }

    @Test
    fun `biometric cancel emits no effect`() =
        runTest {
            viewModel.effect.test {
                viewModel.handleIntent(LoginContract.Intent.BiometricResult(BiometricAuthOutcome.Canceled))
                expectNoEvents()
            }
        }
}
