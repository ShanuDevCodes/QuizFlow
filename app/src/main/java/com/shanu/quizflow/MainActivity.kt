package com.shanu.quizflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.presentation.ThemeViewModel
import com.shanu.quizflow.core.ui.components.ThemeToggleButton
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            QuizFlowTheme(themeMode = themeMode) {
                // Placeholder host — replaced by the Navigation 3 QuizFlowHost in Phase 4.
                AppPlaceholder(
                    themeMode = themeMode,
                    onToggleTheme = themeViewModel::onToggleTheme,
                )
            }
        }
    }
}

@Composable
private fun AppPlaceholder(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = { ThemeToggleButton(themeMode = themeMode, onClick = onToggleTheme) },
            )
        },
    ) { innerPadding ->
        Text(
            text = "QuizFlow scaffold ready.",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPlaceholderPreview() {
    QuizFlowTheme(dynamicColor = false) {
        AppPlaceholder(themeMode = ThemeMode.SYSTEM, onToggleTheme = {})
    }
}
