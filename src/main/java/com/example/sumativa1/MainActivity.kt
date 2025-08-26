package com.example.sumativa1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.sumativa1.navigation.NavRouter
import com.example.sumativa1.screens.LoginScreen
import com.example.sumativa1.screens.RegisterScreen
import com.example.sumativa1.screens.ForgotPasswordScreen
import com.example.sumativa1.screens.HomeScreen
import com.example.sumativa1.ui.theme.Sumativa1Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sumativa1Theme {
                Surface(Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRouter.Login.route
                    ) {

                        composable(NavRouter.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(NavRouter.Home.route) {
                                        popUpTo(NavRouter.Home.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(NavRouter.Register.route)
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate(NavRouter.ForgotPassword.route)
                                }
                            )
                        }

                        composable(NavRouter.Register.route) {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate(NavRouter.Login.route) {
                                        popUpTo(NavRouter.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(NavRouter.Login.route)
                                }
                            )
                        }

                        composable(NavRouter.ForgotPassword.route) {
                            ForgotPasswordScreen(
                                onNavigateBackToLogin = {
                                    navController.navigate(NavRouter.Login.route) {
                                        popUpTo(NavRouter.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable(NavRouter.Home.route) {
                            HomeScreen(
                                onLogout = {
                                    navController.navigate(NavRouter.Login.route) {
                                        popUpTo(NavRouter.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
