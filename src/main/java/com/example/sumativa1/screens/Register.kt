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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.AppTopBar
import com.example.sumativa1.R
import com.example.sumativa1.data.model.AuthRepository
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

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
    var loading by remember { mutableStateOf(false) }

    // Lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.registro))

    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { s -> if (s == TextToSpeech.SUCCESS) tts?.language = Locale("es", "CL") }
        onDispose { tts?.shutdown() }
    }
    fun speak(txt: String) = tts?.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "reg-tts")

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

    fun validEmail(v: String) = v.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(v).matches()

    Scaffold(topBar = { AppTopBar(title = "Registro", showBack = true, onBack = onBack) }) { inner ->
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(scroll)
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(composition, iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(180.dp))
            Spacer(Modifier.height(8.dp))
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it; error = null },
                label = { Text("Nombre") },
                isError = error?.contains("nombre", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu nombre") { nombre = it }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar nombre") }
                }
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = apellido, onValueChange = { apellido = it; error = null },
                label = { Text("Apellido") },
                isError = error?.contains("apellido", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu apellido") { apellido = it }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar apellido") }
                }
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = paisExpanded, onDismissRequest = { paisExpanded = false }) {
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
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu correo electrónico") { email = it }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar correo") }
                }
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it; error = null },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = error?.contains("contraseña", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Di tu contraseña") { password = it.replace(" ", "") }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar contraseña") }
                }
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirm, onValueChange = { confirm = it; error = null },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = error?.contains("coinciden", true) == true || error == "Campos obligatorios",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        startDictation("Repite tu contraseña") { confirm = it.replace(" ", "") }
                    }) { Icon(Icons.Filled.KeyboardVoice, contentDescription = "Dictar confirmación") }
                }
            )

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = quiereNoticias, onCheckedChange = { quiereNoticias = it })
                Spacer(Modifier.width(8.dp)); Text("Quiero recibir noticias")
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = {
                    val resumen = "Nombre: $nombre $apellido. País: $pais. Correo: $email."
                    speak(resumen)
                }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Leer resumen")
                }
                if (!error.isNullOrBlank()) {
                    OutlinedButton(onClick = { speak(error!!) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Leer error")
                    }
                }
            }

            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    error = when {
                        nombre.isBlank() || apellido.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank() ->
                            "Campos obligatorios"
                        !validEmail(email) -> "Correo no válido"
                        password != confirm -> "Las contraseñas no coinciden"
                        else -> null
                    }
                    if (error == null) {
                        val fullName = "$nombre $apellido"
                        loading = true
                        scope.launch {
                            val r = repo.register(fullName, email, password)
                            loading = false
                            if (r.isSuccess) onRegisterSuccess()
                            else { error = r.exceptionOrNull()?.message ?: "No se pudo registrar."; speak(error!!) }
                        }
                    } else {
                        speak(error!!)
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (loading) "Creando..." else "Registrarse") }

            Spacer(Modifier.height(12.dp))
            Text(
                "¿Ya tienes cuenta? Inicia sesión",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}
