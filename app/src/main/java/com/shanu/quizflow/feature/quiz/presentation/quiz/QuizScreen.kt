package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.shanu.quizflow.feature.quiz.presentation.quiz.components.OptionCard
import com.shanu.quizflow.feature.quiz.presentation.quiz.components.QuestionProgressBar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.R
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.components.LottieCelebration
import com.shanu.quizflow.core.ui.components.PopIn
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.components.SkipButton
import com.shanu.quizflow.core.ui.components.SpotlightSurface
import com.shanu.quizflow.core.ui.components.StreakBadge
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview
import com.shanu.quizflow.core.ui.rememberStaggeredReveal
import com.shanu.quizflow.core.ui.swipeToSkip
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme

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
    enableStaggerAnimation: Boolean = true,
) {
    val answering = state.phase == Phase.ANSWERING

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                QuizFlowTopBar(
                    title = stringResource(R.string.app_name),
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme,
                    dynamicColorEnabled = dynamicColorEnabled,
                    onToggleDynamicColor = onToggleDynamicColor,
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(Dimens.SpaceMedium),
                ) {
                    SkipButton(onClick = onSkip, enabled = answering)
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.SpaceMedium)
                    .verticalScroll(rememberScrollState())
                    .swipeToSkip(enabled = answering, onSkip = onSkip),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.SpaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.question_progress, state.questionNumber, state.totalQuestions),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                    StreakBadge(currentStreak = state.currentStreak, active = state.streakActive)
                }

                QuestionProgressBar(
                    currentQuestion = state.questionNumber,
                    totalQuestions = state.totalQuestions,
                    revealing = state.phase == Phase.REVEALING,
                    modifier = Modifier.padding(top = Dimens.SpaceSmall, bottom = Dimens.SpaceLarge),
                )

                if (!enableStaggerAnimation) {
                    // No-animation path for tests: render content directly
                    Column {
                        SpotlightSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Dimens.SpaceLarge),
                        ) {
                            Text(
                                text = state.text,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(Dimens.SpaceMedium),
                            )
                        }

                        state.options.forEachIndexed { index, option ->
                            OptionCard(
                                option = option,
                                onClick = { onOptionSelected(index) },
                                modifier = Modifier.padding(bottom = Dimens.SpaceSmall),
                            )
                        }
                    }
                } else {
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
                        val totalSteps = questionState.options.size + 1
                        val revealedCount by rememberStaggeredReveal(
                            key = questionState.questionNumber,
                            stepCount = totalSteps,
                            stepDelayMs = OptionRevealStaggerMs,
                            firstStepDelayMs = InitialRevealDelayMs,
                            initialDelayMs = QuestionExitDurationMs.toLong(),
                        )

                        Column {
                            PopIn(visible = revealedCount >= 1) {
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

                            questionState.options.forEachIndexed { index, option ->
                                PopIn(visible = revealedCount >= index + 2) {
                                    OptionCard(
                                        option = option,
                                        onClick = { onOptionSelected(index) },
                                        modifier = Modifier.padding(bottom = Dimens.SpaceSmall),
                                    )
                                }
                            }
                        }
                    }
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

@ComponentPreviews
@Composable
private fun QuizScreenAnsweringPreview() {
    QuizFlowPreview {
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

@ComponentPreviews
@Composable
private fun QuizScreenRevealingPreview() {
    QuizFlowPreview {
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
