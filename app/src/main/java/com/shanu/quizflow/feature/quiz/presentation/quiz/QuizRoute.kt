package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.feature.quiz.domain.model.QuizResult
import com.shanu.quizflow.feature.quiz.presentation.common.LoadingScreen

@Composable
fun QuizRouteScreen(
    subjectId: String,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onFinished: (QuizResult) -> Unit,
    quizViewModel: QuizViewModel = hiltViewModel(),
) {
    LaunchedEffect(subjectId) {
        quizViewModel.init(subjectId)
    }

    val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        val finishedState = uiState as? QuizUiState.Finished
        if (finishedState != null) {
            onFinished(finishedState.result)
        }
    }

    when (val state = uiState) {
        is QuizUiState.Question -> {
            QuizScreen(
                state = state,
                themeMode = themeMode,
                onToggleTheme = onToggleTheme,
                dynamicColorEnabled = dynamicColorEnabled,
                onToggleDynamicColor = onToggleDynamicColor,
                onOptionSelected = quizViewModel::onOptionSelected,
                onSkip = quizViewModel::onSkip,
            )
        }

        else -> {
            LoadingScreen(
                uiState = state,
                themeMode = themeMode,
                onToggleTheme = onToggleTheme,
                dynamicColorEnabled = dynamicColorEnabled,
                onToggleDynamicColor = onToggleDynamicColor,
                onRetry = quizViewModel::onRetry,
            )
        }
    }
}