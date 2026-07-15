package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class AdvanceQuizUseCaseTest {

    private val useCase = AdvanceQuizUseCase()

    @Test
    fun `advance increments currentIndex by 1`() {
        val session = sampleSession()
        val result = useCase(session)
        assertThat(result.currentIndex).isEqualTo(1)
    }

    @Test
    fun `advancing past the last question makes the session finished`() {
        val session = sampleSession(count = 10).copy(currentIndex = 9)
        val result = useCase(session)
        assertThat(result.isFinished).isTrue()
        assertThat(result.currentQuestion).isNull()
    }

    @Test
    fun `advance does not touch scoring fields`() {
        val session = sampleSession().copy(correctCount = 3, currentStreak = 2, longestStreak = 4, skippedCount = 1)
        val result = useCase(session)
        assertThat(result.correctCount).isEqualTo(3)
        assertThat(result.currentStreak).isEqualTo(2)
        assertThat(result.longestStreak).isEqualTo(4)
        assertThat(result.skippedCount).isEqualTo(1)
    }
}
