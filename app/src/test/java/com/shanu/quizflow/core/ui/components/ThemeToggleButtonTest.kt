package com.shanu.quizflow.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemeToggleButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(themeMode: ThemeMode, onClick: () -> Unit = {}) {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                ThemeToggleButton(themeMode = themeMode, onClick = onClick)
            }
        }
    }

    @Test
    fun `light mode announces switching to dark`() {
        setContent(ThemeMode.LIGHT)
        composeTestRule
            .onNodeWithContentDescription("Light theme active. Tap to switch to dark theme.")
            .assertExists()
    }

    @Test
    fun `dark mode announces switching to automatic`() {
        setContent(ThemeMode.DARK)
        composeTestRule
            .onNodeWithContentDescription("Dark theme active. Tap to switch to automatic theme.")
            .assertExists()
    }

    @Test
    fun `system mode announces switching to light`() {
        setContent(ThemeMode.SYSTEM)
        composeTestRule
            .onNodeWithContentDescription("Automatic theme active. Tap to switch to light theme.")
            .assertExists()
    }

    @Test
    fun `tapping the button invokes the callback`() {
        var clicked = false
        setContent(ThemeMode.SYSTEM, onClick = { clicked = true })

        composeTestRule
            .onNodeWithContentDescription("Automatic theme active. Tap to switch to light theme.")
            .performClick()

        assertThat(clicked).isTrue()
    }
}
