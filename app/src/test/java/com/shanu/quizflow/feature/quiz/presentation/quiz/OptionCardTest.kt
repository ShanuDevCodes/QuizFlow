package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OptionCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(option: OptionUi, onClick: () -> Unit = {}) {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                OptionCard(option = option, onClick = onClick)
            }
        }
    }

    @Test
    fun `DEFAULT option is enabled and clickable`() {
        var clicked = false
        setContent(OptionUi("Paris", OptionState.DEFAULT), onClick = { clicked = true })

        composeTestRule.onNodeWithText("Paris").assertIsEnabled().performClick()

        assertThat(clicked).isTrue()
    }

    @Test
    fun `CORRECT option announces itself as the correct answer and is disabled`() {
        setContent(OptionUi("Paris", OptionState.CORRECT))

        composeTestRule.onNodeWithContentDescription("Paris, correct answer").assertIsNotEnabled()
    }

    @Test
    fun `WRONG option announces itself as the user's incorrect answer and is disabled`() {
        setContent(OptionUi("Berlin", OptionState.WRONG))

        composeTestRule.onNodeWithContentDescription("Berlin, your answer, incorrect").assertIsNotEnabled()
    }

    @Test
    fun `DIMMED option is disabled and not clickable`() {
        var clicked = false
        setContent(OptionUi("Madrid", OptionState.DIMMED), onClick = { clicked = true })

        composeTestRule.onNodeWithContentDescription("Madrid").assertIsNotEnabled().performClick()

        assertThat(clicked).isFalse()
    }
}
