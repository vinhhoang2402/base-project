package com.demo.projectbase.feature.auth.presentation.register

import app.cash.turbine.test
import com.demo.projectbase.core.testing.MainDispatcherRule
import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.usecase.ValidateConfirmPasswordUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidatePasswordUseCase
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
class RegisterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        viewModel =
            RegisterViewModel(
                validatePassword = ValidatePasswordUseCase(),
                validateConfirmPassword = ValidateConfirmPasswordUseCase(),
            )
    }

    // --- Initial state ---

    @Test
    fun `initial state has empty fields and disabled button`() {
        val state = viewModel.state.value
        assertEquals("", state.password)
        assertEquals("", state.confirmPassword)
        assertNull(state.passwordError)
        assertNull(state.confirmPasswordError)
        assertFalse(state.isRegisterEnabled)
    }

    // --- Password validation ---

    @Test
    fun `entering short password shows too short error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Abc1!"))
        assertEquals(R.string.error_password_too_short, viewModel.state.value.passwordError)
    }

    @Test
    fun `entering password over 20 chars shows too long error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Abcdefgh123456789012!"))
        assertEquals(R.string.error_password_too_long, viewModel.state.value.passwordError)
    }

    @Test
    fun `entering password without uppercase shows no uppercase error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("abcde123!"))
        assertEquals(R.string.error_password_no_uppercase, viewModel.state.value.passwordError)
    }

    @Test
    fun `entering password without digit shows no digit error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Abcdefgh!"))
        assertEquals(R.string.error_password_no_digit, viewModel.state.value.passwordError)
    }

    @Test
    fun `entering password without special char shows no special char error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Abcdefg1"))
        assertEquals(R.string.error_password_no_special_char, viewModel.state.value.passwordError)
    }

    @Test
    fun `entering valid password clears password error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("short"))
        assertNotNull(viewModel.state.value.passwordError)

        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Secure1@"))
        assertNull(viewModel.state.value.passwordError)
    }

    // --- Confirm password validation ---

    @Test
    fun `mismatched confirm password shows mismatch error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Secure1@"))
        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("Different1!"))
        assertEquals(R.string.error_passwords_mismatch, viewModel.state.value.confirmPasswordError)
    }

    @Test
    fun `matching confirm password clears confirm error`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Secure1@"))
        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("Different1!"))
        assertNotNull(viewModel.state.value.confirmPasswordError)

        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("Secure1@"))
        assertNull(viewModel.state.value.confirmPasswordError)
    }

    // --- Register button enabled ---

    @Test
    fun `register button disabled when password invalid`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("weak"))
        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("weak"))
        assertFalse(viewModel.state.value.isRegisterEnabled)
    }

    @Test
    fun `register button disabled when passwords do not match`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Secure1@"))
        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("Different1!"))
        assertFalse(viewModel.state.value.isRegisterEnabled)
    }

    @Test
    fun `register button enabled when password valid and passwords match`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Secure1@"))
        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("Secure1@"))
        assertTrue(viewModel.state.value.isRegisterEnabled)
    }

    @Test
    fun `register button disabled again when valid password is changed to invalid`() {
        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("Secure1@"))
        viewModel.handleIntent(RegisterContract.Intent.ConfirmPasswordChanged("Secure1@"))
        assertTrue(viewModel.state.value.isRegisterEnabled)

        viewModel.handleIntent(RegisterContract.Intent.PasswordChanged("weak"))
        assertFalse(viewModel.state.value.isRegisterEnabled)
    }

    // --- Effects ---

    @Test
    fun `OpenTmdbSignup emits OpenBrowser effect with TMDB url`() =
        runTest {
            viewModel.effect.test {
                viewModel.handleIntent(RegisterContract.Intent.OpenTmdbSignup)
                val effect = awaitItem()
                assertTrue(effect is RegisterContract.Effect.OpenBrowser)
                assertEquals("https://www.themoviedb.org/signup", (effect as RegisterContract.Effect.OpenBrowser).url)
            }
        }

    @Test
    fun `NavigateBack emits NavigateBack effect`() =
        runTest {
            viewModel.effect.test {
                viewModel.handleIntent(RegisterContract.Intent.NavigateBack)
                assertEquals(RegisterContract.Effect.NavigateBack, awaitItem())
            }
        }
}
