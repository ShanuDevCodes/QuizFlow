package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.R
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.components.LottieCelebration
import com.shanu.quizflow.core.ui.components.PopIn
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.rememberStaggeredReveal
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.feature.quiz.domain.model.QuizResult

private const val CelebrationScoreThreshold = 0.8f
private const val RevealStaggerMs = 120L
private const val RevealStepCount = 5

@Composable
fun ResultsScreen(
    result: QuizResult,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scoreFraction = remember(result) { if (result.total == 0) 0f else result.correct.toFloat() / result.total }
    val celebrate = scoreFraction >= CelebrationScoreThreshold

    val revealedCount by rememberStaggeredReveal(
        key = result,
        stepCount = RevealStepCount,
        stepDelayMs = RevealStaggerMs,
    )

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                QuizFlowTopBar(
                    title = stringResource(R.string.results_title),
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme,
                    dynamicColorEnabled = dynamicColorEnabled,
                    onToggleDynamicColor = onToggleDynamicColor,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(Dimens.SpaceLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                PopIn(visible = revealedCount >= 1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (celebrate) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(bottom = Dimens.SpaceSmall),
                            )
                        }
                        Text(
                            text = stringResource(
                                if (celebrate) R.string.results_great_job else R.string.results_quiz_complete,
                            ),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = Dimens.SpaceLarge),
                        )
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Dimens.SpaceLarge)) {
                        PopIn(visible = revealedCount >= 2) {
                            StatRow(
                                icon = Icons.Filled.CheckCircle,
                                label = stringResource(R.string.stat_correct_label),
                                value = stringResource(R.string.stat_correct_value, result.correct, result.total),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.SpaceExtraSmall))
                        PopIn(visible = revealedCount >= 3) {
                            StatRow(
                                icon = Icons.Filled.LocalFireDepartment,
                                label = stringResource(R.string.stat_longest_streak_label),
                                value = result.longestStreak.toString(),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.SpaceExtraSmall))
                        PopIn(visible = revealedCount >= 4) {
                            StatRow(
                                icon = Icons.Filled.SkipNext,
                                label = stringResource(R.string.stat_skipped_label),
                                value = result.skipped.toString(),
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                }

                PopIn(visible = revealedCount >= 5, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onRestart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.SpaceExtraLarge),
                    ) {
                        Text(stringResource(R.string.restart_quiz_button))
                    }
                }
            }
        }

        if (celebrate) {
            LottieCelebration(
                assetFileName = "result_confetti.json",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpaceExtraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = containerColor,
                modifier = Modifier.size(Dimens.StatIconContainerSize),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(Dimens.StatIconSize),
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = Dimens.SpaceMedium),
            )
        }
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultsScreenHighScorePreview() {
    QuizFlowTheme(dynamicColor = false) {
        ResultsScreen(
            result = QuizResult(correct = 9, total = 10, skipped = 0, longestStreak = 5),
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onRestart = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultsScreenLowScorePreview() {
    QuizFlowTheme(dynamicColor = false) {
        ResultsScreen(
            result = QuizResult(correct = 4, total = 10, skipped = 2, longestStreak = 2),
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onRestart = {},
        )
    }
}
