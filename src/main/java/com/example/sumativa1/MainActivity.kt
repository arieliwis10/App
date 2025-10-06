package com.example.sumativa1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sumativa1.navigation.NavRouter
import com.example.sumativa1.screens.Forgot
import com.example.sumativa1.screens.Home
import com.example.sumativa1.screens.Login
import com.example.sumativa1.screens.Register
import com.example.sumativa1.screens.SpeakScreen
import com.example.sumativa1.screens.WriteScreen
import com.example.sumativa1.ui.theme.Sumativa1Theme
import com.example.sumativa1.screens.FindDeviceScreen // 👈 importa tu pantalla de ubicación
import com.example.sumativa1.ui.settings.LocalAppSettings // 👈 asegúrate de tener este provider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sumativa1Theme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNav()
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRouter.Login.route
    ) {
        composable(NavRouter.Login.route) {
            Login(
                onLoginSuccess = {
                    navController.navigate(NavRouter.Home.route) {
                        popUpTo(NavRouter.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRouter.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(NavRouter.Forgot.route) }
            )
        }

        composable(NavRouter.Register.route) {
            Register(
                onBack = { navController.navigateUp() },
                onRegisterSuccess = {
                    navController.navigate(NavRouter.Login.route) {
                        popUpTo(NavRouter.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavRouter.Forgot.route) {
            Forgot(onBack = { navController.navigateUp() })
        }

        composable(NavRouter.Home.route) {
            Home(
                onLogout = {
                    navController.navigate(NavRouter.Login.route) {
                        popUpTo(NavRouter.Home.route) { inclusive = true }
                    }
                },
                onFindDevice = {
                    navController.navigate(NavRouter.FindDevice.route)
                }
            )
        }

        composable(NavRouter.Write.route) {
            WriteScreen(onBack = { navController.popBackStack() })
        }

        composable(NavRouter.Speak.route) {
            SpeakScreen(onBack = { navController.popBackStack() })
        }

        composable(NavRouter.FindDevice.route) {
            FindDeviceScreen(onBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit = {},
    onToggleTheme: (() -> Unit)? = null,
    onFontInc:  (() -> Unit)? = null,
    onFontDec:  (() -> Unit)? = null,
    onFontReset:(() -> Unit)? = null
) {
    val settings = LocalAppSettings.current
    var menuOpen by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            }
        },
        actions = {
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Filled.Settings, contentDescription = "Ajustes")
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Modo día/noche") },
                    onClick = {
                        (onToggleTheme ?: settings::toggleTheme).invoke()
                        menuOpen = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Letra chica") },
                    onClick = {
                        (onFontDec ?: settings::setSmall).invoke()
                        menuOpen = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Letra grande") },
                    onClick = {
                        (onFontInc ?: settings::setLarge).invoke()
                        menuOpen = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Restablecer mediana") },
                    onClick = {
                        (onFontReset ?: settings::setMedium).invoke()
                        menuOpen = false
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
