package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.feature.quiz.domain.model.QuizResult
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResultsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(result: QuizResult, onRestart: () -> Unit = {}) {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                ResultsScreen(
                    result = result,
                    themeMode = ThemeMode.SYSTEM,
                    onToggleTheme = {},
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                    onRestart = onRestart,
                )
            }
        }
    }

    @Test
    fun `high score shows the celebratory headline`() {
        setContent(QuizResult(correct = 9, total = 10, skipped = 0, longestStreak = 5))

        composeTestRule.onNodeWithText("Great job!").assertExists()
        composeTestRule.onNodeWithText("Quiz complete").assertDoesNotExist()
    }

    @Test
    fun `low score shows the plain headline, no celebration`() {
        setContent(QuizResult(correct = 4, total = 10, skipped = 2, longestStreak = 2))

        composeTestRule.onNodeWithText("Quiz complete").assertExists()
        composeTestRule.onNodeWithText("Great job!").assertDoesNotExist()
    }

    @Test
    fun `stat rows reflect the result`() {
        setContent(QuizResult(correct = 4, total = 10, skipped = 2, longestStreak = 3))

        composeTestRule.onNodeWithText("4 / 10").assertExists()
        composeTestRule.onNodeWithText("3").assertExists()
        composeTestRule.onNodeWithText("2").assertExists()
    }

    @Test
    fun `skipped row is always shown, even when nothing was skipped`() {
        setContent(QuizResult(correct = 10, total = 10, skipped = 0, longestStreak = 10))

        composeTestRule.onNodeWithText("Skipped").assertExists()
        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun `tapping Restart Quiz invokes the callback`() {
        var restarted = false
        setContent(QuizResult(correct = 4, total = 10, skipped = 2, longestStreak = 2), onRestart = { restarted = true })

        composeTestRule.onNodeWithText("Restart Quiz").performClick()

        assertThat(restarted).isTrue()
    }
}
