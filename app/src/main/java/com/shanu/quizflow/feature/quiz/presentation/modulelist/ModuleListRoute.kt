package com.shanu.quizflow.feature.quiz.presentation.modulelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shanu.quizflow.core.settings.domain.model.ThemeMode

@Composable
fun ModuleListRouteScreen(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onStartModule: (String) -> Unit,
    onReviewModule: (String) -> Unit,
    viewModel: ModuleListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ModuleListScreen(
        state = uiState,
        themeMode = themeMode,
        onToggleTheme = onToggleTheme,
        dynamicColorEnabled = dynamicColorEnabled,
        onToggleDynamicColor = onToggleDynamicColor,
        onStartModule = onStartModule,
        onReviewModule = onReviewModule,
        onRetry = viewModel::sync,
    )
}
