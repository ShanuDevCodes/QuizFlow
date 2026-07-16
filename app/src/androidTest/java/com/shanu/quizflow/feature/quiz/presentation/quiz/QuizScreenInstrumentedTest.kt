package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun questionState(phase: Phase) = QuizUiState.Question(
        questionNumber = 1,
        totalQuestions = 10,
        text = "What is the capital of France?",
        options = listOf(
            OptionUi("Berlin", if (phase == Phase.REVEALING) OptionState.DIMMED else OptionState.DEFAULT),
            OptionUi("Paris", if (phase == Phase.REVEALING) OptionState.CORRECT else OptionState.DEFAULT),
            OptionUi("Madrid", if (phase == Phase.REVEALING) OptionState.DIMMED else OptionState.DEFAULT),
            OptionUi("Rome", if (phase == Phase.REVEALING) OptionState.DIMMED else OptionState.DEFAULT),
        ),
        phase = phase,
        currentStreak = 0,
        streakActive = false,
    )

    @Test
    fun tappingAnOptionInvokesTheCallbackWithItsIndex() {
        var selectedIndex: Int? = null
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuizScreen(
                    state = questionState(Phase.ANSWERING),
                    themeMode = ThemeMode.SYSTEM,
                    onToggleTheme = {},
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                    onOptionSelected = { selectedIndex = it },
                    onSkip = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Paris").performClick()

        assertEquals(1, selectedIndex)
    }

    @Test
    fun swipingLeftDuringAnsweringInvokesSkip() {
        var skipped = false
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuizScreen(
                    state = questionState(Phase.ANSWERING),
                    themeMode = ThemeMode.SYSTEM,
                    onToggleTheme = {},
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                    onOptionSelected = {},
                    onSkip = { skipped = true },
                )
            }
        }

        composeTestRule.onRoot().performTouchInput { swipeLeft() }

        assertTrue(skipped)
    }
}
