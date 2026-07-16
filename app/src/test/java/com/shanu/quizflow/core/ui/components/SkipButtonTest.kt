package com.shanu.quizflow.core.ui.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SkipButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `enabled skip button is clickable and invokes the callback`() {
        var clicked = false
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                SkipButton(onClick = { clicked = true }, enabled = true)
            }
        }

        composeTestRule.onNodeWithText("Skip").assertHasClickAction().performClick()

        assertThat(clicked).isTrue()
    }

    @Test
    fun `disabled skip button does not invoke the callback`() {
        var clicked = false
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                SkipButton(onClick = { clicked = true }, enabled = false)
            }
        }

        composeTestRule.onNodeWithText("Skip").assertIsNotEnabled().performClick()

        assertThat(clicked).isFalse()
    }
}
