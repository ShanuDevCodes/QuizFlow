package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.feature.quiz.domain.model.QuizResult
import com.shanu.quizflow.feature.quiz.presentation.navigation.ResultsRoute

@Composable
fun ResultsRouteScreen(
    route: ResultsRoute,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onFinished: () -> Unit,
    onRestarted: () -> Unit,
    resultsViewModel: ResultsViewModel = hiltViewModel(),
) {
    LaunchedEffect(resultsViewModel, route.subjectId) {
        resultsViewModel.init(route.subjectId)
    }

    val resultState by resultsViewModel.resultState.collectAsStateWithLifecycle()

    val result = if (route.isReview) {
        resultState ?: return
    } else {
        QuizResult(
            correct = route.correct,
            total = route.total,
            skipped = route.skipped,
            longestStreak = route.streak,
            highScore = resultState?.highScore ?: route.correct,
        )
    }

    ResultsScreen(
        result = result,
        themeMode = themeMode,
        onToggleTheme = onToggleTheme,
        dynamicColorEnabled = dynamicColorEnabled,
        onToggleDynamicColor = onToggleDynamicColor,
        isReview = route.isReview,
        onFinish = onFinished,
        onRestart = onRestarted,
    )
}
