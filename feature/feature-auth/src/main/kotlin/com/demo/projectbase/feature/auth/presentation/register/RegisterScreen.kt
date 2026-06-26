package com.demo.projectbase.feature.auth.presentation.register

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.projectbase.core.ui.base.BaseScreen
import com.demo.projectbase.core.ui.component.AppButton
import com.demo.projectbase.feature.auth.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegistered: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val context = LocalContext.current

    BaseScreen(
        baseEffect = viewModel.baseEffect,
        onSessionExpired = onSessionExpired,
        isLoading = false,
    ) { padding ->
        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is RegisterContract.Effect.OpenBrowser -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
                        context.startActivity(intent)
                    }
                    RegisterContract.Effect.NavigateBack -> onNavigateBack()
                }
            }
        }

        RegisterContent(
            onIntent = viewModel::handleIntent,
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun RegisterContent(
    onIntent: (RegisterContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
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
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.register_tmdb_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppButton(
            text = stringResource(R.string.register_tmdb_button),
            onClick = { onIntent(RegisterContract.Intent.OpenTmdbSignup) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.register_have_account),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = onNavigateBack) {
                Text(stringResource(R.string.register_to_login))
            }
        }
    }
}
