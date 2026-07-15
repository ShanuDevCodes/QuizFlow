package com.shanu.quizflow.feature.quiz.presentation.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizUiState
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizViewModel

@Composable
fun LoadingRouteScreen(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onLoaded: () -> Unit,
    quizViewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is QuizUiState.Question) onLoaded()
    }

    LoadingScreen(
        uiState = uiState,
        themeMode = themeMode,
        onToggleTheme = onToggleTheme,
        dynamicColorEnabled = dynamicColorEnabled,
        onToggleDynamicColor = onToggleDynamicColor,
        onRetry = quizViewModel::onRetry,
    )
}
