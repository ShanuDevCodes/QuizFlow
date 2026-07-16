package com.shanu.quizflow.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

@Composable
fun rememberStaggeredReveal(
    key: Any?,
    stepCount: Int,
    stepDelayMs: Long,
    firstStepDelayMs: Long = stepDelayMs,
    initialDelayMs: Long = 0L,
): State<Int> {
    val revealedCount = remember(key) { mutableIntStateOf(0) }
    LaunchedEffect(key) {
        if (initialDelayMs > 0) delay(initialDelayMs)
        repeat(stepCount) { step ->
            delay(if (step == 0) firstStepDelayMs else stepDelayMs)
            revealedCount.intValue = step + 1
        }
    }
    return revealedCount
}
