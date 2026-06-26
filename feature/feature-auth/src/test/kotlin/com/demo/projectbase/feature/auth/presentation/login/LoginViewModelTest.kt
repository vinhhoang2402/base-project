package com.demo.projectbase.feature.auth.presentation.login

import app.cash.turbine.test
import com.demo.projectbase.core.network.NetworkException
import com.demo.projectbase.core.testing.MainDispatcherRule
import com.demo.projectbase.core.ui.base.BaseEffect
import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.feature.auth.domain.model.User
import com.demo.projectbase.feature.auth.domain.repository.AuthRepository
import com.demo.projectbase.feature.auth.domain.usecase.LoginUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidateEmailUseCase
import com.demo.projectbase.feature.auth.domain.usecase.ValidatePasswordUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(authRepository),
            validateEmail = ValidateEmailUseCase(),
            validatePassword = ValidatePasswordUseCase(),
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
    fun `successful login emits NavigateToHome effect`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.success(
            User("1", "tmdbuser", "Test")
        )

        viewModel.effect.test {
            viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
            viewModel.handleIntent(LoginContract.Intent.PasswordChanged("password123"))
            viewModel.handleIntent(LoginContract.Intent.Submit)

            assertEquals(LoginContract.Effect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `login with wrong credentials sets passwordError`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns
            Result.failure(NetworkException.Unauthorized())

        viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
        viewModel.handleIntent(LoginContract.Intent.PasswordChanged("wrongpass"))
        viewModel.handleIntent(LoginContract.Intent.Submit)

        assertEquals(R.string.error_wrong_credentials, viewModel.state.value.passwordError)
    }

    @Test
    fun `generic error emits baseEffect ShowError`() = runTest {
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
    fun `isLoading is false after login completes`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.success(
            User("1", "tmdbuser", "Test")
        )

        viewModel.handleIntent(LoginContract.Intent.UsernameChanged("tmdbuser"))
        viewModel.handleIntent(LoginContract.Intent.PasswordChanged("password123"))
        viewModel.handleIntent(LoginContract.Intent.Submit)

        assertTrue(!viewModel.state.value.isLoading)
    }
}
