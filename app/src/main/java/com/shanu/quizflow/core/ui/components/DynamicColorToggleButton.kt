package com.shanu.quizflow.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DynamicColorToggleButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (enabled) Icons.Filled.Palette else Icons.Outlined.Palette
    val description = if (enabled) {
        "Dynamic wallpaper color on. Tap to switch to the app's default colors."
    } else {
        "App default colors active. Tap to switch to dynamic wallpaper color."
    }

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
