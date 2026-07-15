package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizUiState
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizViewModel

@Composable
fun ResultsRouteScreen(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onRestarted: () -> Unit,
    quizViewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is QuizUiState.Question) onRestarted()
    }

    val finishedState = uiState as? QuizUiState.Finished ?: return

    ResultsScreen(
        result = finishedState.result,
        themeMode = themeMode,
        onToggleTheme = onToggleTheme,
        dynamicColorEnabled = dynamicColorEnabled,
        onToggleDynamicColor = onToggleDynamicColor,
        onRestart = quizViewModel::onRestart,
    )
}
