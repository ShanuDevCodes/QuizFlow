package com.shanu.quizflow.feature.quiz.presentation.common

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shanu.quizflow.R
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.ui.components.QuizFlowTopBar
import com.shanu.quizflow.core.ui.theme.Dimens
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
                title = stringResource(R.string.app_name),
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
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = Dimens.SpaceMedium),
                        )
                        val messageText = uiState.message.formatArg?.let { arg ->
                            stringResource(uiState.message.messageRes, arg)
                        } ?: stringResource(uiState.message.messageRes)
                        Text(text = messageText)
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.padding(top = Dimens.SpaceMedium),
                        ) {
                            Text(stringResource(R.string.retry_button))
                        }
                    }
                }

                else -> QuizSkeleton()
            }
        }
    }
}
