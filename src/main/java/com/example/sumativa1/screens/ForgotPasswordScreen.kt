package com.example.sumativa1.screens

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.R
import com.example.sumativa1.data.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.recuperar)
    )

    fun isValidEmail(value: String) =
        value.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(value).matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 16.dp)
        )

        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Ingresa tu correo y te enviaremos instrucciones (simulado).",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                message = null
                isError = false
            },
            label = { Text("Correo electrónico") },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (isError) Text("Ingresa un correo válido.")
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    isError = true
                    message = null
                    return@Button
                }

                AuthRepository.requestPasswordRecovery(email)
                isError = false
                message = "Si el correo está registrado, recibirás un mensaje de recuperación."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar instrucciones")
        }

        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = message!!,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Volver a iniciar sesión",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNavigateBackToLogin() }
        )
    }
}
