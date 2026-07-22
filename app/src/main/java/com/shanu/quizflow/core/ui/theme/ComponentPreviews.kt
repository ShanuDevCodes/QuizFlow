package com.shanu.quizflow.core.ui.theme

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ComponentPreviews

@Composable
fun QuizFlowPreview(
    content: @Composable () -> Unit,
) {
    QuizFlowTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}
