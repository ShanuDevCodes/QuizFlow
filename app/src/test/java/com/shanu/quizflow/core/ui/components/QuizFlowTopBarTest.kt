package com.shanu.quizflow.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
class QuizFlowTopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `renders the title and the theme toggle`() {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuizFlowTopBar(
                    title = "QuizFlow",
                    themeMode = ThemeMode.SYSTEM,
                    onToggleTheme = {},
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                )
            }
        }

        composeTestRule.onNodeWithText("QuizFlow").assertExists()
        composeTestRule
            .onNodeWithContentDescription("Automatic theme active. Tap to switch to light theme.")
            .assertExists()
    }

    @Test
    fun `theme toggle in the top bar invokes its callback`() {
        var toggled = false
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                QuizFlowTopBar(
                    title = "Results",
                    themeMode = ThemeMode.LIGHT,
                    onToggleTheme = { toggled = true },
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Light theme active. Tap to switch to dark theme.")
            .performClick()

        assertThat(toggled).isTrue()
    }
}
