package com.demo.projectbase

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.demo.projectbase.core.network.SecurePreferencesManager
import com.demo.projectbase.core.ui.theme.ProjectBaseTheme
import com.demo.projectbase.feature.home.presentation.HomeFragment
import com.demo.projectbase.navigation.AppNavHost
import org.koin.android.ext.android.inject

// Toggle to switch between Compose and XML
private const val USE_COMPOSE = false

class MainActivity : AppCompatActivity() {

    private val securePrefs: SecurePreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (USE_COMPOSE) {
            enableEdgeToEdge()
            setContent {
                ProjectBaseTheme {
                    AppNavHost(onClearSession = securePrefs::clearTokens)
                }
            }
        } else {
            setContentView(R.layout.activity_main)
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment())
                    .commit()
            }
        }
    }
}
