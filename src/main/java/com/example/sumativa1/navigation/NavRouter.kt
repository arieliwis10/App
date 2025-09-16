package com.example.sumativa1.navigation

sealed class NavRouter(val route: String) {
    data object Login : NavRouter("login")
    data object Register : NavRouter("register")
    data object Forgot : NavRouter("forgot")
    data object Home : NavRouter("home")
}