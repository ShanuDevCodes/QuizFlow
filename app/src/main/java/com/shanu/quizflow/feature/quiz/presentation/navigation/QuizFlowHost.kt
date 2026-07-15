package com.shanu.quizflow.feature.quiz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.feature.quiz.presentation.loading.LoadingRouteScreen
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizRouteScreen
import com.shanu.quizflow.feature.quiz.presentation.results.ResultsRouteScreen

@Composable
fun QuizFlowHost(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(LoadingRoute)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        entryProvider = entryProvider {
            entry<LoadingRoute> {
                LoadingRouteScreen(
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme,
                    dynamicColorEnabled = dynamicColorEnabled,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onLoaded = {
                        backStack.clear()
                        backStack.add(QuizRoute)
                    },
                )
            }
            entry<QuizRoute> {
                QuizRouteScreen(
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme,
                    dynamicColorEnabled = dynamicColorEnabled,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onFinished = {
                        backStack.clear()
                        backStack.add(ResultsRoute)
                    },
                )
            }
            entry<ResultsRoute> {
                ResultsRouteScreen(
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme,
                    dynamicColorEnabled = dynamicColorEnabled,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onRestarted = {
                        backStack.clear()
                        backStack.add(QuizRoute)
                    },
                )
            }
        },
    )
}
