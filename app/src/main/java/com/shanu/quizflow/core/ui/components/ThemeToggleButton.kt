package com.shanu.quizflow.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shanu.quizflow.core.settings.domain.model.ThemeMode

/**
 * Single-tap control that cycles [ThemeMode] Light -> Dark -> System -> Light.
 * Icon reflects the *current* mode; the content description states what tapping will do next,
 * so screen readers announce the resulting action rather than just the current state.
 */
@Composable
fun ThemeToggleButton(
    themeMode: ThemeMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, description) = when (themeMode) {
        ThemeMode.LIGHT -> Icons.Filled.LightMode to "Light theme active. Tap to switch to dark theme."
        ThemeMode.DARK -> Icons.Filled.DarkMode to "Dark theme active. Tap to switch to automatic theme."
        ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto to "Automatic theme active. Tap to switch to light theme."
    }

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
