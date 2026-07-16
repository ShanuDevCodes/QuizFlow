package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RemainingLoadingDelayMsTest {

    @Test
    fun `waits the full minimum when nothing has elapsed`() {
        assertThat(remainingLoadingDelayMs(minDurationMs = 600L, elapsedMs = 0L)).isEqualTo(600L)
    }

    @Test
    fun `waits only the remainder when the load already took part of the minimum`() {
        assertThat(remainingLoadingDelayMs(minDurationMs = 600L, elapsedMs = 250L)).isEqualTo(350L)
    }

    @Test
    fun `adds no extra delay when the load already took longer than the minimum`() {
        assertThat(remainingLoadingDelayMs(minDurationMs = 600L, elapsedMs = 800L)).isEqualTo(0L)
    }

    @Test
    fun `adds no extra delay when the load took exactly the minimum`() {
        assertThat(remainingLoadingDelayMs(minDurationMs = 600L, elapsedMs = 600L)).isEqualTo(0L)
    }
}
