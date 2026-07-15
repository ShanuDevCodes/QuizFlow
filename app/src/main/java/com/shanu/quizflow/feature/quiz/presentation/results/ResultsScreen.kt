package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.components.LottieCelebration
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.feature.quiz.domain.model.QuizResult

private const val CelebrationScoreThreshold = 0.8f

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

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                QuizFlowTopBar(
                    title = "Results",
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
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    ) + fadeIn(animationSpec = tween()),
                ) {
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
                            text = if (celebrate) "Great job!" else "Quiz complete",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = Dimens.SpaceLarge),
                        )

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(Dimens.SpaceLarge)) {
                                ResultRow(label = "Correct", value = "${result.correct} / ${result.total}")
                                ResultRow(label = "Longest streak", value = result.longestStreak.toString())
                                if (result.skipped > 0) {
                                    ResultRow(label = "Skipped", value = result.skipped.toString())
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.SpaceExtraLarge),
                ) {
                    Text("Restart Quiz")
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
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpaceExtraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
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
