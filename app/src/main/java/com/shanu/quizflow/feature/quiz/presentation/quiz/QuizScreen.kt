package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.components.LottieCelebration
import com.shanu.quizflow.core.ui.components.PopIn
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.components.SkipButton
import com.shanu.quizflow.core.ui.components.SpotlightSurface
import com.shanu.quizflow.core.ui.components.StreakBadge
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import kotlinx.coroutines.delay

private val SwipeToSkipThreshold = 96.dp
private const val QuestionExitDurationMs = 300
private const val OptionRevealStaggerMs = 180L
private const val InitialRevealDelayMs = 50L

@Composable
fun QuizScreen(
    state: QuizUiState.Question,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val answering = state.phase == Phase.ANSWERING
    val thresholdPx = with(LocalDensity.current) { SwipeToSkipThreshold.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                QuizFlowTopBar(
                    title = "QuizFlow",
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
                    .padding(Dimens.SpaceMedium)
                    .let { base ->
                        if (answering) {
                            base.pointerInput(onSkip) {
                                var totalDrag = 0f
                                detectHorizontalDragGestures(
                                    onDragStart = { totalDrag = 0f },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDrag += dragAmount
                                    },
                                    onDragEnd = {
                                        if (totalDrag < -thresholdPx) onSkip()
                                    },
                                )
                            }
                        } else {
                            base
                        }
                    },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Question ${state.questionNumber} of ${state.totalQuestions}",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    StreakBadge(currentStreak = state.currentStreak, active = state.streakActive)
                }

                QuestionProgressBar(
                    currentQuestion = state.questionNumber,
                    totalQuestions = state.totalQuestions,
                    revealing = state.phase == Phase.REVEALING,
                    modifier = Modifier.padding(top = Dimens.SpaceSmall, bottom = Dimens.SpaceLarge),
                )

                AnimatedContent(
                    targetState = state,
                    contentKey = { it.questionNumber },
                    transitionSpec = {
                        fadeIn(animationSpec = tween(durationMillis = 1, delayMillis = QuestionExitDurationMs)) togetherWith
                            (
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis = QuestionExitDurationMs, easing = FastOutLinearInEasing),
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                ) + fadeOut(animationSpec = tween(durationMillis = QuestionExitDurationMs))
                            ) using null
                    },
                    label = "questionContent",
                ) { questionState ->
                    val revealedCount = remember { mutableIntStateOf(0) }
                    LaunchedEffect(Unit) {
                        delay(QuestionExitDurationMs.toLong())
                        repeat(questionState.options.size + 1) { step ->
                            delay(if (step == 0) InitialRevealDelayMs else OptionRevealStaggerMs)
                            revealedCount.intValue = step + 1
                        }
                    }

                    Column {
                        PopIn(visible = revealedCount.intValue >= 1) {
                            SpotlightSurface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Dimens.SpaceLarge),
                            ) {
                                Text(
                                    text = questionState.text,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(Dimens.SpaceMedium),
                                )
                            }
                        }

                        val optionSpacing = remember { Dimens.SpaceSmall }
                        questionState.options.forEachIndexed { index, option ->
                            PopIn(visible = revealedCount.intValue >= index + 2) {
                                OptionCard(
                                    option = option,
                                    onClick = { onOptionSelected(index) },
                                    modifier = Modifier.padding(bottom = optionSpacing),
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    SkipButton(onClick = onSkip, enabled = answering)
                }
            }
        }

        if (state.streakActive) {
            LottieCelebration(
                assetFileName = "streak_confetti.json",
                modifier = Modifier
                    .scale(2f)
                    .fillMaxSize(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizScreenAnsweringPreview() {
    QuizFlowTheme(dynamicColor = false) {
        QuizScreen(
            state = previewQuestionState(phase = Phase.ANSWERING),
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onOptionSelected = {},
            onSkip = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizScreenRevealingPreview() {
    QuizFlowTheme(dynamicColor = false) {
        QuizScreen(
            state = previewQuestionState(phase = Phase.REVEALING, streak = 3),
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onOptionSelected = {},
            onSkip = {},
        )
    }
}

private fun previewQuestionState(phase: Phase, streak: Int = 1) = QuizUiState.Question(
    questionNumber = 3,
    totalQuestions = 10,
    text = "You added a hidden gesture that unlocks a secret screen in your app. How should users discover it?",
    options = listOf(
        OptionUi("Berlin", if (phase == Phase.REVEALING) OptionState.WRONG else OptionState.DEFAULT),
        OptionUi("Paris", if (phase == Phase.REVEALING) OptionState.CORRECT else OptionState.DEFAULT),
        OptionUi("Madrid", if (phase == Phase.REVEALING) OptionState.DIMMED else OptionState.DEFAULT),
        OptionUi("Rome", if (phase == Phase.REVEALING) OptionState.DIMMED else OptionState.DEFAULT),
    ),
    phase = phase,
    currentStreak = streak,
    streakActive = streak >= 3,
)
