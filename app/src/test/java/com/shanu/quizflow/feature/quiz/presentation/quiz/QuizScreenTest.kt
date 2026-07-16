package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuizScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun questionState(phase: Phase, streak: Int = 1) = QuizUiState.Question(
        questionNumber = 3,
        totalQuestions = 10,
        text = "What is the capital of France?",
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

    private fun setContent(state: QuizUiState.Question, onOptionSelected: (Int) -> Unit = {}, onSkip: () -> Unit = {}) {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuizScreen(
                    state = state,
                    themeMode = ThemeMode.SYSTEM,
                    onToggleTheme = {},
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                    onOptionSelected = onOptionSelected,
                    onSkip = onSkip,
                )
            }
        }
    }

    @Test
    fun `shows the question number and total`() {
        setContent(questionState(phase = Phase.ANSWERING))

        composeTestRule.onNodeWithText("Question 3 of 10").assertExists()
    }

    @Test
    fun `shows the question text and all options`() {
        setContent(questionState(phase = Phase.ANSWERING))

        composeTestRule.onNodeWithText("What is the capital of France?").assertExists()
        composeTestRule.onNodeWithText("Paris").assertExists()
        composeTestRule.onNodeWithText("Berlin").assertExists()
    }

    @Test
    fun `Skip is enabled while answering`() {
        setContent(questionState(phase = Phase.ANSWERING))

        composeTestRule.onNodeWithText("Skip").assertIsEnabled()
    }

    @Test
    fun `Skip is disabled while revealing`() {
        setContent(questionState(phase = Phase.REVEALING))

        composeTestRule.onNodeWithText("Skip").assertIsNotEnabled()
    }

    @Test
    fun `tapping an option while answering invokes the callback with its index`() {
        var selectedIndex: Int? = null
        setContent(questionState(phase = Phase.ANSWERING), onOptionSelected = { selectedIndex = it })

        composeTestRule.onNodeWithText("Paris").performClick()

        assertThat(selectedIndex).isEqualTo(1)
    }

    @Test
    fun `tapping Skip while answering invokes the callback`() {
        var skipped = false
        setContent(questionState(phase = Phase.ANSWERING), onSkip = { skipped = true })

        val skipNode = composeTestRule.onNodeWithText("Skip").fetchSemanticsNode()
        val onClickAction = skipNode.config[androidx.compose.ui.semantics.SemanticsActions.OnClick].action
        val invokeResult = onClickAction?.invoke()
        java.io.File("/tmp/semantics_tree_debug.txt").writeText(
            "invokeResult=$invokeResult, skippedAfterDirectInvoke=$skipped",
        )
        composeTestRule.waitForIdle()

        assertThat(skipped).isTrue()
    }
}
