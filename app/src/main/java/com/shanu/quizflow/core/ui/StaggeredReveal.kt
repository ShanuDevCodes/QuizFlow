package com.shanu.quizflow.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun rememberStaggeredReveal(
    key: Any?,
    stepCount: Int,
    stepDelayMs: Long,
    firstStepDelayMs: Long = stepDelayMs,
    initialDelayMs: Long = 0L,
): State<Int> {
    val isPreview = LocalInspectionMode.current
    val revealedCount = remember(key) { mutableIntStateOf(if (isPreview) stepCount else 0) }
    LaunchedEffect(key) {
        if (isPreview) return@LaunchedEffect
        if (initialDelayMs > 0) delay(initialDelayMs.milliseconds)
        repeat(stepCount) { step ->
            delay((if (step == 0) firstStepDelayMs else stepDelayMs).milliseconds)
            revealedCount.intValue = step + 1
        }
    }
    return revealedCount
}
