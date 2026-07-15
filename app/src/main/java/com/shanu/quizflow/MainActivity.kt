package com.shanu.quizflow

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.presentation.ThemeViewModel
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.core.ui.theme.resolveDarkTheme
import com.shanu.quizflow.feature.quiz.presentation.navigation.QuizFlowHost
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
            val dynamicColorEnabled by themeViewModel.dynamicColorEnabled.collectAsStateWithLifecycle()
            val darkTheme = resolveDarkTheme(themeMode)

            // enableEdgeToEdge() in onCreate only reads the system's dark-mode config once;
            // it doesn't react to this app's own Light/Dark/System preference, so re-apply it
            // here whenever the effective theme changes.
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                )
            }

            QuizFlowTheme(themeMode = themeMode, dynamicColor = dynamicColorEnabled) {
                QuizFlowHost(
                    themeMode = themeMode,
                    onToggleTheme = themeViewModel::onToggleTheme,
                    dynamicColorEnabled = dynamicColorEnabled,
                    onToggleDynamicColor = themeViewModel::onToggleDynamicColor,
                )
            }
        }
    }
}
