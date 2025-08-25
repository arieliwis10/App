package com.example.sumativa1.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sumativa1.data.AuthRepository // Importar AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBackToLogin: () -> Unit // Para volver a la pantalla de Login
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) } // Para mensajes al usuario
    var isError by remember { mutableStateOf(false) } // Para colorear el mensaje
    val authRepository = AuthRepository

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recuperar Contraseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Ingresa tu email y te enviaremos (simulado) instrucciones para restablecer tu contraseña.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = message != null && isError // Marcar si hay un mensaje de error
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (message != null) {
            Text(
                message!!,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                message = null // Reset message
                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    message = "Por favor, ingresa un email válido."
                    isError = true
                } else {
                    if (authRepository.requestPasswordRecovery(email)) {
                        message = "Si el email está registrado, recibirás un correo de recuperación."
                        isError = false
                    } else {
                        // Aunque requestPasswordRecovery devuelva false si el usuario no existe,
                        // por seguridad, es mejor dar un mensaje genérico.
                        message = "Si el email está registrado, recibirás un correo de recuperación."
                        isError = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Instrucciones")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Volver a Iniciar Sesión",
            modifier = Modifier.clickable { onNavigateBackToLogin() },
            color = MaterialTheme.colorScheme.primary
        )
    }
}
