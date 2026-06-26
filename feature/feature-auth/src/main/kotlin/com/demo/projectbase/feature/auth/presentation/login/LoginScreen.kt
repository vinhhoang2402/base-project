package com.demo.projectbase.feature.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.projectbase.core.ui.component.AppButton
import com.demo.projectbase.core.ui.component.AppTextField
import com.demo.projectbase.core.ui.component.PasswordField
import com.demo.projectbase.feature.auth.R
import com.demo.projectbase.core.ui.base.BaseScreen

object LoginTestTags {
    const val EMAIL_FIELD = "login_email_field"
    const val PASSWORD_FIELD = "login_password_field"
    const val SUBMIT_BUTTON = "login_submit_button"
}

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BaseScreen(
        baseEffect = viewModel.baseEffect,
        onSessionExpired = onSessionExpired,
        isLoading = state.isLoading,
    ) { padding ->
        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    LoginContract.Effect.NavigateToHome -> onNavigateToHome()
                }
            }
        }

        LoginContent(
            state = state,
            onIntent = viewModel::handleIntent,
            onNavigateToRegister = onNavigateToRegister,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun LoginContent(
    state: LoginContract.State,
    onIntent: (LoginContract.Intent) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = state.username,
            onValueChange = { onIntent(LoginContract.Intent.UsernameChanged(it)) },
            label = stringResource(R.string.field_email),
            placeholder = stringResource(R.string.field_email_hint),
            errorMessage = state.usernameError?.let { stringResource(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.testTag(LoginTestTags.EMAIL_FIELD),
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            value = state.password,
            onValueChange = { onIntent(LoginContract.Intent.PasswordChanged(it)) },
            label = stringResource(R.string.field_password),
            errorMessage = state.passwordError?.let { stringResource(it) },
            modifier = Modifier.testTag(LoginTestTags.PASSWORD_FIELD),
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = stringResource(R.string.login_button),
            onClick = { onIntent(LoginContract.Intent.Submit) },
            enabled = state.isSubmitEnabled,
            isLoading = state.isLoading,
            modifier = Modifier.testTag(LoginTestTags.SUBMIT_BUTTON),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.login_no_account),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.login_to_register))
            }
        }
    }
}
