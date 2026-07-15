package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.shanu.quizflow.core.ui.components.StreakBadge
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StreakBadgeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `announces the count and unlit state when not active`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                StreakBadge(currentStreak = 2, active = false)
            }
        }

        composeTestRule.onNodeWithContentDescription("Streak: 2 correct answers in a row").assertExists()
    }

    @Test
    fun `announces the count and lit state when active`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                StreakBadge(currentStreak = 5, active = true)
            }
        }

        composeTestRule.onNodeWithContentDescription("Streak badge lit: 5 correct answers in a row").assertExists()
    }
}
