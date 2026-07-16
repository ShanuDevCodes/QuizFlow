package com.shanu.quizflow.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicColorToggleButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `enabled state announces switching to default colors`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                DynamicColorToggleButton(enabled = true, onClick = {})
            }
        }
        composeTestRule
            .onNodeWithContentDescription("Dynamic wallpaper color on. Tap to switch to the app's default colors.")
            .assertExists()
    }

    @Test
    fun `disabled state announces switching to dynamic color`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                DynamicColorToggleButton(enabled = false, onClick = {})
            }
        }
        composeTestRule
            .onNodeWithContentDescription("App default colors active. Tap to switch to dynamic wallpaper color.")
            .assertExists()
    }

    @Test
    fun `tapping the button invokes the callback`() {
        var clicked = false
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                DynamicColorToggleButton(enabled = false, onClick = { clicked = true })
            }
        }

        composeTestRule
            .onNodeWithContentDescription("App default colors active. Tap to switch to dynamic wallpaper color.")
            .performClick()

        assertThat(clicked).isTrue()
    }
}
