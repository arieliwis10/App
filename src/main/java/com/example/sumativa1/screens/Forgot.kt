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
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.AppTopBar
import com.example.sumativa1.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Forgot(onBack: () -> Unit) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    // Lottie (opcional)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.recuperar))

    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { s ->
            if (s == TextToSpeech.SUCCESS) tts?.language = Locale("es", "CL")
        }
        onDispose { tts?.shutdown() }
    }
    fun speak(txt: String) = tts?.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "forgot-tts")

    // STT
    var onSpeechResult by remember { mutableStateOf<(String) -> Unit>({}) }
    val sttLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) onSpeechResult(spoken.trim())
        }
    }
    fun startDictation(prompt: String, onResult: (String) -> Unit) {
        onSpeechResult = onResult
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
        sttLauncher.launch(i)
    }

    fun isValidEmail(value: String) =
        value.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(value).matches()

    Scaffold(topBar = { AppTopBar(title = "Recuperar Contraseña", showBack = true, onBack = onBack) }) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(composition, iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(200.dp))

            Spacer(Modifier.height(16.dp))
            Text("Ingresa tu correo y te enviaremos instrucciones (simulado).")

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; message = null; isError = false },
                label = { Text("Correo electrónico") },
                isError = isError,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu correo electrónico") { email = it }
                    }) {
                        Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar correo")
                    }
                },
                supportingText = { if (isError) Text("Ingresa un correo válido.") }
            )

            if (!message.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    message!!,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    val texto = if (email.isBlank()) "El campo correo está vacío."
                    else "Correo ingresado: $email."
                    speak("Pantalla de recuperar contraseña. $texto")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Leer en voz alta")
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (!isValidEmail(email)) {
                        isError = true; message = null
                        speak("El correo ingresado no es válido.")
                    } else {
                        isError = false
                        message = "Si el correo está registrado, recibirás un mensaje de recuperación."
                        speak(message!!)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Enviar instrucciones") }

            Spacer(Modifier.height(16.dp))
            Text(
                "Volver a iniciar sesión",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}
