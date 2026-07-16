package com.shanu.quizflow.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shanu.quizflow.R

@Composable
fun DynamicColorToggleButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (enabled) Icons.Filled.Palette else Icons.Outlined.Palette
    val description = stringResource(
        if (enabled) R.string.dynamic_color_on_description else R.string.dynamic_color_off_description,
    )

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
