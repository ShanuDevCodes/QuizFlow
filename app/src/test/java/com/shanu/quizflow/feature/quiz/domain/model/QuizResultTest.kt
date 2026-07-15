package com.shanu.quizflow.feature.quiz.domain.model

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class QuizResultTest {

    @Test
    fun `toResult maps session tallies directly`() {
        val session = sampleSession(count = 10).copy(
            correctCount = 7,
            skippedCount = 1,
            longestStreak = 4,
        )

        val result = session.toResult()

        assertThat(result).isEqualTo(QuizResult(correct = 7, total = 10, skipped = 1, longestStreak = 4))
    }

    @Test
    fun `toResult on a fresh session is all zeros with the full question count`() {
        val result = sampleSession(count = 10).toResult()

        assertThat(result).isEqualTo(QuizResult(correct = 0, total = 10, skipped = 0, longestStreak = 0))
    }
}
