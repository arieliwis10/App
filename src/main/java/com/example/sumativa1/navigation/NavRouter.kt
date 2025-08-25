package com.example.sumativa1.navigation

sealed class NavRouter(val route: String) {
    data object Onboard1 : NavRouter("onboard1") // Pantalla de Bienvenida
    data object Login : NavRouter("login")
    data object Register : NavRouter("register")
    data object ForgotPassword : NavRouter("forgot_password")
    data object Home : NavRouter("home") // Pantalla principal después del login/registro
}