package com.demo.projectbase.feature.auth.presentation.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit4.runners.AndroidJUnit4
import com.demo.projectbase.core.ui.theme.ProjectBaseTheme
import com.demo.projectbase.feature.auth.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(
        state: LoginContract.State = LoginContract.State(),
        onIntent: (LoginContract.Intent) -> Unit = {},
        onNavigateToRegister: () -> Unit = {},
    ) {
        composeRule.setContent {
            ProjectBaseTheme {
                LoginContent(
                    state = state,
                    onIntent = onIntent,
                    onNavigateToRegister = onNavigateToRegister,
                )
            }
        }
    }

    @Test
    fun loginScreen_showsAllFields() {
        setContent()
        composeRule.onNodeWithText("Username").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Login").assertIsDisplayed()
        composeRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsUsernameError() {
        setContent(state = LoginContract.State(usernameError = R.string.error_email_empty))
        composeRule.onNodeWithText("Username cannot be empty").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsPasswordError() {
        setContent(state = LoginContract.State(passwordError = R.string.error_password_too_short))
        composeRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun loginScreen_submitButtonDisabledWhenLoading() {
        setContent(state = LoginContract.State(isSubmitEnabled = true, isLoading = true))
        composeRule.onNodeWithTag(LoginTestTags.SUBMIT_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun loginScreen_submitButtonEnabledWhenNotLoading() {
        setContent(state = LoginContract.State(isSubmitEnabled = true, isLoading = false))
        composeRule.onNodeWithTag(LoginTestTags.SUBMIT_BUTTON).assertIsEnabled()
    }

    @Test
    fun loginScreen_usernameInputDispatchesIntent() {
        val intents = mutableListOf<LoginContract.Intent>()
        setContent(onIntent = { intents.add(it) })

        composeRule.onNodeWithTag(LoginTestTags.EMAIL_FIELD).performTextInput("tmdbuser")

        assert(intents.any { it is LoginContract.Intent.UsernameChanged })
    }

    @Test
    fun loginScreen_submitButtonDispatchesSubmitIntent() {
        val intents = mutableListOf<LoginContract.Intent>()
        setContent(onIntent = { intents.add(it) })

        composeRule.onNodeWithTag(LoginTestTags.SUBMIT_BUTTON).performClick()

        assert(intents.contains(LoginContract.Intent.Submit))
    }

    @Test
    fun loginScreen_registerLinkCallsCallback() {
        var navigateCalled = false
        setContent(onNavigateToRegister = { navigateCalled = true })

        composeRule.onNodeWithText("Register").performClick()

        assert(navigateCalled)
    }
}
