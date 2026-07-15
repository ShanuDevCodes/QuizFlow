package com.shanu.quizflow.feature.quiz.presentation.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.theme.Dimens
import com.shanu.quizflow.core.ui.theme.QuizFlowTheme
import com.shanu.quizflow.feature.quiz.presentation.quiz.QuizUiState

@Composable
fun LoadingScreen(
    uiState: QuizUiState,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    dynamicColorEnabled: Boolean,
    onToggleDynamicColor: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            QuizFlowTopBar(
                title = "QuizFlow",
                themeMode = themeMode,
                onToggleTheme = onToggleTheme,
                dynamicColorEnabled = dynamicColorEnabled,
                onToggleDynamicColor = onToggleDynamicColor,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.SpaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (uiState) {
                is QuizUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = Dimens.SpaceMedium),
                        )
                        Text(text = uiState.message)
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.padding(top = Dimens.SpaceMedium),
                        ) {
                            Text("Retry")
                        }
                    }
                }

                else -> QuizSkeleton()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenPreview() {
    QuizFlowTheme(dynamicColor = false) {
        LoadingScreen(
            uiState = QuizUiState.Loading,
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenErrorPreview() {
    QuizFlowTheme(dynamicColor = false) {
        LoadingScreen(
            uiState = QuizUiState.Error("Couldn't reach the network. Check your connection and try again."),
            themeMode = ThemeMode.SYSTEM,
            onToggleTheme = {},
            dynamicColorEnabled = false,
            onToggleDynamicColor = {},
            onRetry = {},
        )
    }
}
