package com.shanu.quizflow.feature.quiz.presentation.modulelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview
import com.shanu.quizflow.feature.quiz.domain.model.ModuleStatus
import com.shanu.quizflow.R
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.getValue
import com.shanu.quizflow.core.ui.components.PopIn
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.rememberStaggeredReveal
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.feature.quiz.presentation.modulelist.components.ModuleCard
import com.shanu.quizflow.feature.quiz.presentation.modulelist.components.OverallProgressCard
import com.shanu.quizflow.feature.quiz.presentation.modulelist.components.QuickResumeBar
import com.shanu.quizflow.feature.quiz.presentation.modulelist.components.ShimmerModuleCard

@Composable
fun ModuleListScreen(
    state: ModuleListUiState,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onStartModule: (String) -> Unit,
    onReviewModule: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            QuizFlowTopBar(
                title = stringResource(R.string.module_list_title),
                themeMode = themeMode,
                onToggleTheme = onToggleTheme,
                dynamicColorEnabled = dynamicColorEnabled,
                onToggleDynamicColor = onToggleDynamicColor,
            )
        },
    ) { innerPadding ->
        val extraBottomPadding = if (state.activeSessionModule != null) 120.dp else 0.dp
        val listContentPadding = PaddingValues(
            start = Dimens.SpaceMedium + innerPadding.calculateStartPadding(layoutDirection),
            top = Dimens.SpaceMedium,
            end = Dimens.SpaceMedium + innerPadding.calculateEndPadding(layoutDirection),
            bottom = Dimens.SpaceMedium + innerPadding.calculateBottomPadding() + extraBottomPadding,
        )

        val revealedCount by rememberStaggeredReveal(
            key = state.modules.size,
            stepCount = state.modules.size + 1,
            stepDelayMs = 90L,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        ) {
            when {
                state.isLoading && state.modules.isEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = listContentPadding,
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
                    ) {
                        items(4) {
                            ShimmerModuleCard()
                        }
                    }
                }

                state.syncError != null && state.modules.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.SpaceLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = state.syncError,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.padding(top = Dimens.SpaceMedium),
                        ) {
                            Text(stringResource(R.string.retry_button))
                        }
                    }
                }

                state.modules.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.module_list_empty),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = listContentPadding,
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMedium),
                    ) {
                        item(key = "overall_progress") {
                            val isRevealed = revealedCount >= 1
                            PopIn(visible = isRevealed) {
                                OverallProgressCard(
                                    progress = state.overallProgress,
                                    visible = isRevealed,
                                )
                            }
                        }

                        itemsIndexed(
                            items = state.modules,
                            key = { _, module -> module.id },
                        ) { index, module ->
                            val isRevealed = revealedCount >= index + 2
                            PopIn(visible = isRevealed) {
                                ModuleCard(
                                    module = module,
                                    onStartClick = { onStartModule(module.id) },
                                    onReviewClick = { onReviewModule(module.id) },
                                    visible = isRevealed,
                                )
                            }
                        }
                    }
                }
            }

            QuickResumeBar(
                module = state.activeSessionModule,
                onResumeClick = onStartModule,
                visible = state.modules.isEmpty() || (revealedCount >= state.modules.size + 1),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = innerPadding.calculateBottomPadding()),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun ModuleListScreenPreview() {
    QuizFlowPreview {
        ModuleListScreen(
            state = ModuleListUiState(
                modules = listOf(
                    ModuleUi("1", "Android Basics", "Core concepts", ModuleStatus.NOT_STARTED, isCompleted = false, totalQuestions = 10, lastScore = null, highScore = null),
                    ModuleUi("2", "Jetpack Compose", "Declarative UI", ModuleStatus.IN_PROGRESS, isCompleted = false, totalQuestions = 10, lastScore = null, highScore = null),
                    ModuleUi("3", "Testing", "Unit & UI tests", ModuleStatus.COMPLETED, isCompleted = true, totalQuestions = 10, lastScore = 9, highScore = 9),
                ),
                activeSessionModule = ModuleUi("2", "Jetpack Compose", "Declarative UI", ModuleStatus.IN_PROGRESS, isCompleted = false, totalQuestions = 10, lastScore = null, highScore = null),
                overallProgress = OverallProgress(1, 3, 33, 9, 9f),
                isLoading = false,
            ),
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onStartModule = {},
            onReviewModule = {},
            onRetry = {},
        )
    }
}

