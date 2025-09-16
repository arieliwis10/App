package com.example.sumativa1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.sumativa1.ui.settings.AppSettingsController
import com.example.sumativa1.ui.settings.FontSizeMode
import com.example.sumativa1.ui.settings.LocalAppSettings

private fun androidx.compose.material3.Typography.scale(mode: FontSizeMode): androidx.compose.material3.Typography {
    val f = when (mode) { FontSizeMode.Small -> 0.9f; FontSizeMode.Medium -> 1.2f; FontSizeMode.Large -> 1.6f }
    return androidx.compose.material3.Typography(
        displayLarge = displayLarge.copy(fontSize = displayLarge.fontSize * f),
        displayMedium = displayMedium.copy(fontSize = displayMedium.fontSize * f),
        displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize * f),
        headlineLarge = headlineLarge.copy(fontSize = headlineLarge.fontSize * f),
        headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize * f),
        headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize * f),
        titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize * f),
        titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize * f),
        titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize * f),
        bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize * f),
        bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize * f),
        bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize * f),
        labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize * f),
        labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize * f),
        labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize * f),
    )
}

@Composable
fun Sumativa1Theme(content: @Composable () -> Unit) {
    var dark by rememberSaveable { mutableStateOf(false) }
    var mode by rememberSaveable { mutableStateOf(FontSizeMode.Medium) }

    val settings = remember { AppSettingsController(dark, mode) }

    // IMPORTANTE: usa esta forma de LaunchedEffect sin nombres
    LaunchedEffect(settings.darkTheme, settings.fontMode) {
        dark = settings.darkTheme
        mode = settings.fontMode
    }

    val colors = if (settings.darkTheme) darkColorScheme() else lightColorScheme()
    val typography = androidx.compose.material3.Typography().scale(settings.fontMode)

    CompositionLocalProvider(LocalAppSettings provides settings) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            content = content
        )
    }
}
