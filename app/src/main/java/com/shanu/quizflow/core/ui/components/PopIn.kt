package com.shanu.quizflow.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import com.shanu.quizflow.core.ui.theme.ComponentPreviews
import com.shanu.quizflow.core.ui.theme.QuizFlowPreview

private const val PopInInitialScale = 0.85f

@Composable
fun PopIn(visible: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val isPreview = LocalInspectionMode.current
    val effectiveVisible = visible || isPreview
    val scale by animateFloatAsState(
        targetValue = if (effectiveVisible) 1f else PopInInitialScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "popInScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (effectiveVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "popInAlpha",
    )

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        },
    ) {
        content()
    }
}

@ComponentPreviews
@Composable
private fun PopInPreview() {
    QuizFlowPreview {
        PopIn(visible = true) {
            Text(
                text = "PopIn Animation Content",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
