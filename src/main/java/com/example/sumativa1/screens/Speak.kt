package com.example.sumativa1.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sumativa1.data.model.AuthRepository
import com.example.sumativa1.data.model.Message
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val repo = remember { AuthRepository(context) }
    val scope = rememberCoroutineScope()

    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "CL")
            }
        }
        onDispose { tts?.shutdown() }
    }

    // Carga/observa mensajes
    var mensajes by remember { mutableStateOf<List<Message>>(emptyList()) }
    LaunchedEffect(Unit) {
        // si tienes observeMensajes() puedes usarlo con collectAsState;
        // aquí cargo una vez para simpleza:
        mensajes = repo.getMensajes()
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts-id")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (mensajes.isNotEmpty()) speak(mensajes.joinToString(". ") { it.text }) },
                    enabled = mensajes.isNotEmpty()
                ) { Text("Leer todo") }

                OutlinedButton(onClick = {
                    scope.launch { mensajes = repo.getMensajes() }
                }) { Text("Actualizar") }
            }

            if (mensajes.isEmpty()) {
                Text("No hay mensajes para leer.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mensajes) { m ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { speak(m.text) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(m.text, style = MaterialTheme.typography.bodyLarge)
                                if (m.lat != null && m.lng != null) {
                                    Spacer(Modifier.height(4.dp))
                                    Text("Ubicación: ${m.lat}, ${m.lng}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
