package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode

@Composable
fun QuizRouteScreen(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onFinished: () -> Unit,
    quizViewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is QuizUiState.Finished) onFinished()
    }

    val questionState = uiState as? QuizUiState.Question ?: return

    QuizScreen(
        state = questionState,
        themeMode = themeMode,
        onToggleTheme = onToggleTheme,
        dynamicColorEnabled = dynamicColorEnabled,
        onToggleDynamicColor = onToggleDynamicColor,
        onOptionSelected = quizViewModel::onOptionSelected,
        onSkip = quizViewModel::onSkip,
    )
}
