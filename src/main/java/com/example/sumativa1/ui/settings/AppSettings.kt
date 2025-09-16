package com.example.sumativa1.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

enum class FontSizeMode { Small, Medium, Large }

class AppSettingsController(
    dark: Boolean,
    size: FontSizeMode
) {
    var darkTheme by mutableStateOf(dark)
    var fontMode  by mutableStateOf(size)

    fun toggleTheme() { darkTheme = !darkTheme }
    fun setSmall()    { fontMode  = FontSizeMode.Small }
    fun setMedium()   { fontMode  = FontSizeMode.Medium } // “restablecer mediana”
    fun setLarge()    { fontMode  = FontSizeMode.Large }
}

// CompositionLocal para acceder al controlador desde cualquier pantalla/topbar
val LocalAppSettings = staticCompositionLocalOf<AppSettingsController> {
    error("LocalAppSettings no fue provisto por Sumativa1Theme")
}


