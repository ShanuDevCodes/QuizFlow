package com.shanu.quizflow.core.ui

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val SwipeToSkipThresholdDp = 96.dp

@Composable
fun Modifier.swipeToSkip(enabled: Boolean, onSkip: () -> Unit): Modifier {
    if (!enabled) return this
    val thresholdPx = with(LocalDensity.current) { SwipeToSkipThresholdDp.toPx() }
    return this.pointerInput(onSkip) {
        var totalDrag = 0f
        detectHorizontalDragGestures(
            onDragStart = { totalDrag = 0f },
            onHorizontalDrag = { change, dragAmount ->
                change.consume()
                totalDrag += dragAmount
            },
            onDragEnd = {
                if (totalDrag < -thresholdPx) onSkip()
            },
        )
    }
}
