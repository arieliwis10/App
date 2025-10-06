package com.example.sumativa1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sumativa1.ui.components.OsmMap
import com.example.sumativa1.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDeviceScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Buscar dispositivo",
                showBack = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Ubicación aproximada del dispositivo:")
            OsmMap(
                lat = -33.4489,
                lng = -70.6693,
                zoom = 15.0
            )
            Button(onClick = { /* aquí puedes activar funciones extra, como centrar */ }) {
                Text("Actualizar ubicación")
            }
        }
    }
}
