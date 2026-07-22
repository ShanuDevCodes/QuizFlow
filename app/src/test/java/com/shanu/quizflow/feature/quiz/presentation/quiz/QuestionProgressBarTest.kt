package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.shanu.quizflow.feature.quiz.presentation.quiz.components.ProgressSegmentFillTagPrefix
import com.shanu.quizflow.feature.quiz.presentation.quiz.components.ProgressSegmentTag
import com.shanu.quizflow.feature.quiz.presentation.quiz.components.QuestionProgressBar

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuestionProgressBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `renders one segment per question`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuestionProgressBar(currentQuestion = 1, totalQuestions = 10)
            }
        }

        composeTestRule.onAllNodesWithTag(ProgressSegmentTag).assertCountEquals(10)
    }

    @Test
    fun `exactly one segment is active, matching the current question`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuestionProgressBar(currentQuestion = 7, totalQuestions = 10)
            }
        }

        composeTestRule.onAllNodesWithTag("${ProgressSegmentFillTagPrefix}active").assertCountEquals(1)
    }

    @Test
    fun `segments before the current question are completed and segments after are upcoming`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuestionProgressBar(currentQuestion = 7, totalQuestions = 10)
            }
        }

        composeTestRule.onAllNodesWithTag("${ProgressSegmentFillTagPrefix}completed").assertCountEquals(6)
        composeTestRule.onAllNodesWithTag("${ProgressSegmentFillTagPrefix}upcoming").assertCountEquals(3)
    }

    @Test
    fun `the first question has no completed segments and the last has no upcoming segments`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuestionProgressBar(currentQuestion = 1, totalQuestions = 10)
            }
        }

        composeTestRule.onAllNodesWithTag("${ProgressSegmentFillTagPrefix}completed").assertCountEquals(0)
        composeTestRule.onAllNodesWithTag("${ProgressSegmentFillTagPrefix}active").assertCountEquals(1)
        composeTestRule.onAllNodesWithTag("${ProgressSegmentFillTagPrefix}upcoming").assertCountEquals(9)
    }
}
