package com.shanu.quizflow.feature.quiz.presentation.loading

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.R
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.feature.quiz.presentation.quiz.AppErrorMessage
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoadingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(uiState: QuizUiState, onRetry: () -> Unit = {}) {
        composeTestRule.setContent {
            QuizFlowTheme(dynamicColor = false) {
                LoadingScreen(
                    uiState = uiState,
                    themeMode = ThemeMode.SYSTEM,
                    onToggleTheme = {},
                    dynamicColorEnabled = false,
                    onToggleDynamicColor = {},
                    onRetry = onRetry,
                )
            }
        }
    }

    @Test
    fun `Loading state shows no retry action`() {
        setContent(QuizUiState.Loading)

        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun `Error state shows the message and a Retry action`() {
        setContent(QuizUiState.Error(AppErrorMessage(R.string.error_network)))

        val expectedMessage = ApplicationProvider.getApplicationContext<android.content.Context>()
            .getString(R.string.error_network)
        composeTestRule.onNodeWithText(expectedMessage).assertExists()
        composeTestRule.onNodeWithText("Retry").assertExists()
    }

    @Test
    fun `tapping Retry invokes the callback`() {
        var retried = false
        setContent(QuizUiState.Error(AppErrorMessage(R.string.error_network)), onRetry = { retried = true })

        composeTestRule.onNodeWithText("Retry").performClick()

        assertThat(retried).isTrue()
    }
}
