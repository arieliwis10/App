package com.example.sumativa1.screens

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.R
import com.example.sumativa1.AppTopBar
import com.example.sumativa1.data.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val s = com.example.sumativa1.ui.settings.LocalAppSettings.current
    Text(if (s.darkTheme) "DARK=ON" else "DARK=OFF")
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))


    fun isValidEmail(v: String) =
        v.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(v).matches()

    Scaffold(
        topBar = { AppTopBar(title = "Mi Primera App", showBack = false) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)  // <- importante para no quedar bajo el TopBar
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(220.dp)
            )

            Spacer(Modifier.height(12.dp))
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = { Text("Correo") },
                isError = error != null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = error != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    when {
                        !isValidEmail(email) -> error = "Correo no válido."
                        password.isBlank()  -> error = "Ingresa tu contraseña."
                        AuthRepository.loginUser(email, password) -> onLoginSuccess()
                        else -> error = "Correo o contraseña incorrectos."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }

            Spacer(Modifier.height(10.dp))

            Text(
                "¿Olvidaste tu contraseña?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "¿No tienes cuenta? Regístrate",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
