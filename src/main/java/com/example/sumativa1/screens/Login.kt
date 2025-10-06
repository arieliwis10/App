package com.example.sumativa1.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.R
import com.example.sumativa1.AppTopBar
import com.example.sumativa1.data.model.AuthRepository
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    // ---- Estado UI
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ---- Lottie (opcional, usa tu raw existente)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))

    // ---- TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { st ->
            if (st == TextToSpeech.SUCCESS) tts?.language = Locale("es", "CL")
        }
        onDispose { tts?.shutdown() }
    }
    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "login-tts")
    }

    // ---- STT (un solo launcher; decidimos qué campo rellenar por callback)
    var onDictationResult by remember { mutableStateOf<(String) -> Unit>({}) }
    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) onDictationResult(spoken.trim())
        }
    }
    fun startDictation(prompt: String, onResult: (String) -> Unit) {
        onDictationResult = onResult
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
        sttLauncher.launch(i)
    }

    fun validEmail(v: String) = v.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(v).matches()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Iniciar sesión",
                showBack = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)              // evita quedar bajo la TopBar
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(12.dp))
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Correo electrónico") },
                singleLine = true,
                isError = errorMessage?.contains("correo", true) == true,
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu correo electrónico") { email = it }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar correo") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = errorMessage?.contains("contraseña", true) == true,
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu contraseña") { password = it.replace(" ", "") }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar contraseña") }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            // Botón: Leer en voz alta
            OutlinedButton(
                onClick = {
                    val texto = buildString {
                        append("Pantalla de inicio de sesión. ")
                        append(if (email.isBlank()) "El campo correo está vacío. " else "Correo actual: $email. ")
                        append(if (password.isBlank()) "El campo contraseña está vacío." else "Contraseña ingresada.")
                        errorMessage?.let { append(" Error: $it") }
                    }
                    speak(texto)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Leer en voz alta")
            }

            Spacer(Modifier.height(12.dp))

            // Botón: Iniciar sesión
            Button(
                onClick = {
                    // Validaciones simples
                    when {
                        email.isBlank() || password.isBlank() -> {
                            errorMessage = "Correo y contraseña son obligatorios."
                            speak(errorMessage!!)
                        }
                        !validEmail(email) -> {
                            errorMessage = "El correo ingresado no es válido."
                            speak(errorMessage!!)
                        }
                        else -> {
                            loading = true
                            scope.launch {
                                val result = repo.login(email, password)
                                loading = false
                                if (result.isSuccess) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Credenciales inválidas."
                                    speak(errorMessage!!)
                                }
                            }
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Ingresando…" else "Iniciar sesión")
            }

            Spacer(Modifier.height(16.dp))

            // Acciones secundarias
            Text(
                "¿Olvidaste tu contraseña?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "¿No tienes cuenta? Regístrate",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
