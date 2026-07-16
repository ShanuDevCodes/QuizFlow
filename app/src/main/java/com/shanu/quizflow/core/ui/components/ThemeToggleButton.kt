package com.shanu.quizflow.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shanu.quizflow.R
import com.shanu.quizflow.core.settings.domain.model.ThemeMode

@Composable
fun ThemeToggleButton(
    themeMode: ThemeMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, descriptionRes) = when (themeMode) {
        ThemeMode.LIGHT -> Icons.Filled.LightMode to R.string.theme_light_description
        ThemeMode.DARK -> Icons.Filled.DarkMode to R.string.theme_dark_description
        ThemeMode.SYSTEM -> Icons.Filled.BrightnessAuto to R.string.theme_system_description
    }
    val description = stringResource(descriptionRes)

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
