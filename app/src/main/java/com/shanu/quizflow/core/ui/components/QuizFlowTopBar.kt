package com.shanu.quizflow.core.ui.components

import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shanu.quizflow.core.settings.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizFlowTopBar(
    title: String,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = { Text(title) },
        actions = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DynamicColorToggleButton(enabled = dynamicColorEnabled, onClick = onToggleDynamicColor)
            }
            ThemeToggleButton(themeMode = themeMode, onClick = onToggleTheme)
        },
        modifier = modifier,
    )
}
