package com.example.sumativa1.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.sumativa1.data.model.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var saving by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf<String?>(null) }

    // Launcher para dictado (STT)
    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val result = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = result?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                text = TextFieldValue(
                    if (text.text.isBlank()) spoken else text.text + " " + spoken
                )
            }
        }
    }

    fun startDictation() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla para dictar el texto…")
        }
        sttLauncher.launch(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escribir") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = text,
                onValueChange = { text = it; msg = null },
                label = { Text("Mensaje") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { startDictation() }) {
                    Text("Dictar con voz")
                }
                Button(
                    onClick = {
                        if (text.text.isBlank()) {
                            msg = "Escribe o dicta un mensaje."
                        } else {
                            saving = true
                            scope.launch {
                                // lat/lng opcionales (null por ahora)
                                repo.addMensaje(text.text.trim(), lat = null, lng = null)
                                saving = false
                                text = TextFieldValue("")
                                msg = "Mensaje guardado."
                            }
                        }
                    },
                    enabled = !saving
                ) {
                    Text(if (saving) "Guardando…" else "Guardar")
                }
            }

            if (!msg.isNullOrBlank()) {
                Text(msg!!, color = MaterialTheme.colorScheme.primary)
            }

            Divider()

            // Vista rápida: últimos guardados
            Text("Recientes", style = MaterialTheme.typography.titleMedium)
            val (refreshKey, setRefresh) = remember { mutableStateOf(0) }
            // Fuerza recomposición simple después de guardar
            LaunchedEffect(msg) { if (msg == "Mensaje guardado.") setRefresh(refreshKey + 1) }

            var list by remember(refreshKey) { mutableStateOf(emptyList<com.example.sumativa1.data.model.Message>()) }
            LaunchedEffect(refreshKey) {
                list = repo.getMensajes()
            }
            if (list.isEmpty()) {
                Text("No hay mensajes aún.")
            } else {
                list.take(3).forEach {
                    Text("• ${it.text}")
                }
            }
        }
    }
}
