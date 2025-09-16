package com.example.sumativa1.screens

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.AppTopBar
import com.example.sumativa1.R
import com.example.sumativa1.data.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val paises = listOf("Chile", "Ecuador", "Colombia")
    var paisExpanded by remember { mutableStateOf(false) }
    var pais by remember { mutableStateOf(paises.first()) }

    var quiereNoticias by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.registro))

    fun validEmail(v: String) = v.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(v).matches()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registro",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        // Hacemos scroll y evitamos que el teclado tape el botón
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scroll)
                .imePadding()                // <- mueve el contenido sobre el teclado
                .navigationBarsPadding()     // <- por si hay gestos/barras
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Encabezado / Lottie ---
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(180.dp) // un poco más chica para dejar espacio al botón
            )
            Spacer(Modifier.height(12.dp))
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // --- Campos ---
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it; error = null },
                label = { Text("Nombre") },
                isError = error?.contains("nombre", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido, onValueChange = { apellido = it; error = null },
                label = { Text("Apellido") },
                isError = error?.contains("apellido", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = paisExpanded,
                onExpandedChange = { paisExpanded = !paisExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = pais,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("País") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paisExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = paisExpanded,
                    onDismissRequest = { paisExpanded = false }
                ) {
                    paises.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = { pais = p; paisExpanded = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it; error = null },
                label = { Text("Correo") },
                isError = error?.contains("correo", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it; error = null },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = error?.contains("contraseña", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirm, onValueChange = { confirm = it; error = null },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = error?.contains("coinciden", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(checked = quiereNoticias, onCheckedChange = { quiereNoticias = it })
                Spacer(Modifier.width(8.dp))
                Text("Quiero recibir noticias")
            }

            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // --- Empujar el botón hacia abajo pero visible con scroll ---
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    error = when {
                        nombre.isBlank() || apellido.isBlank() ||
                                email.isBlank() || password.isBlank() || confirm.isBlank() ->
                            "Campos obligatorios"
                        !validEmail(email) -> "Correo no válido"
                        password != confirm -> "Las contraseñas no coinciden"
                        AuthRepository.registerUser(email, password) -> null
                        else -> "El correo ya está registrado"
                    }
                    if (error == null) onRegisterSuccess()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrarse") }

            Spacer(Modifier.height(12.dp))

            Text(
                "¿Ya tienes cuenta? Inicia sesión",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}

