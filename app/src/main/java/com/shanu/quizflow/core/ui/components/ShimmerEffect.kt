package com.shanu.quizflow.core.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * (if (size.width > 0) size.width.toFloat() else 1000f),
        targetValue = 2 * (if (size.width > 0) size.width.toFloat() else 1000f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
        ),
        label = "shimmerAnimation",
    )

    val baseColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val highlightColor = MaterialTheme.colorScheme.surfaceContainerHighest

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                baseColor,
                highlightColor,
                baseColor,
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + (if (size.width > 0) size.width.toFloat() else 500f), size.height.toFloat()),
        ),
    ).onGloballyPositioned {
        size = it.size
    }
}