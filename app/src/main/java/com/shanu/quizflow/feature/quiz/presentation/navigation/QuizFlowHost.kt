package com.shanu.quizflow.feature.quiz.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.feature.quiz.presentation.modulelist.ModuleListRouteScreen
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizRouteScreen
import com.shanu.quizflow.feature.quiz.presentation.results.ResultsRouteScreen

private const val NavAnimDurationMs = 250

@Composable
fun QuizFlowHost(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(ModuleListRoute)

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn(animationSpec = tween(NavAnimDurationMs)) togetherWith fadeOut(animationSpec = tween(NavAnimDurationMs))
            },
            popTransitionSpec = {
                fadeIn(animationSpec = tween(NavAnimDurationMs)) togetherWith fadeOut(animationSpec = tween(NavAnimDurationMs))
            },
            predictivePopTransitionSpec = { _ ->
                fadeIn(animationSpec = tween(NavAnimDurationMs)) togetherWith fadeOut(animationSpec = tween(NavAnimDurationMs))
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<ModuleListRoute> {
                    ModuleListRouteScreen(
                        themeMode = themeMode,
                        onToggleTheme = onToggleTheme,
                        dynamicColorEnabled = dynamicColorEnabled,
                        onToggleDynamicColor = onToggleDynamicColor,
                        onStartModule = { subjectId ->
                            backStack.add(QuizRoute(subjectId))
                        },
                        onReviewModule = { subjectId ->
                            backStack.add(ResultsRoute(subjectId = subjectId, isReview = true))
                        },
                    )
                }
                entry<QuizRoute> { route ->
                    QuizRouteScreen(
                        subjectId = route.subjectId,
                        themeMode = themeMode,
                        onToggleTheme = onToggleTheme,
                        dynamicColorEnabled = dynamicColorEnabled,
                        onToggleDynamicColor = onToggleDynamicColor,
                        onFinished = { result ->
                            backStack.removeLastOrNull()
                            backStack.add(
                                ResultsRoute(
                                    subjectId = route.subjectId,
                                    correct = result.correct,
                                    total = result.total,
                                    skipped = result.skipped,
                                    streak = result.longestStreak,
                                    isReview = false,
                                ),
                            )
                        },
                    )
                }
                entry<ResultsRoute> { route ->
                    ResultsRouteScreen(
                        route = route,
                        themeMode = themeMode,
                        onToggleTheme = onToggleTheme,
                        dynamicColorEnabled = dynamicColorEnabled,
                        onToggleDynamicColor = onToggleDynamicColor,
                        onFinished = {
                            backStack.clear()
                            backStack.add(ModuleListRoute)
                        },
                        onRestarted = {
                            backStack.removeLastOrNull()
                            backStack.add(QuizRoute(route.subjectId))
                        },
                    )
                }
            },
        )
    }
}
