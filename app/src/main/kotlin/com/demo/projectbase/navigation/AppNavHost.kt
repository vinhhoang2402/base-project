package com.demo.projectbase.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.projectbase.feature.auth.presentation.login.LoginScreen
import com.demo.projectbase.feature.auth.presentation.register.RegisterScreen
import com.demo.projectbase.feature.home.presentation.HomeScreen

@Composable
fun AppNavHost(onClearSession: () -> Unit = {}) {
    val navController = rememberNavController()

    fun navigateToLoginAndClearStack() {
        onClearSession()
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSessionExpired = ::navigateToLoginAndClearStack,
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.navigateUp() },
                onRegistered = { navController.navigateUp() },
                onSessionExpired = ::navigateToLoginAndClearStack,
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onSessionExpired = ::navigateToLoginAndClearStack,
            )
        }
    }
}
