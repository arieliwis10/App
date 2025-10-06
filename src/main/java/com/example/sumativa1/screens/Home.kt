package com.example.sumativa1.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.sumativa1.R
import java.util.Locale

@Composable
fun Home(
    onLogout: () -> Unit,
    onFindDevice: () -> Unit
) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.creacion))

    // TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { s -> if (s == TextToSpeech.SUCCESS) tts?.language = Locale("es", "CL") }
        onDispose { tts?.shutdown() }
    }
    fun speak(txt: String) = tts?.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "home-tts")

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
                .size(220.dp)
                .padding(bottom = 24.dp)
        )

        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Has iniciado sesión correctamente.", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { speak("Bienvenido. Puedes buscar tu dispositivo o cerrar sesión.") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.VolumeUp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Leer bienvenida")
        }

        Spacer(Modifier.height(20.dp))
        Button(onClick = onFindDevice, modifier = Modifier.fillMaxWidth()) { Text("Buscar dispositivo") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(0.8f)) { Text("Cerrar sesión") }
    }
}
