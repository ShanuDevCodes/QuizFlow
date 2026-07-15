package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.sampleQuestions
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class RestartQuizUseCaseTest {

    private val useCase = RestartQuizUseCase()

    @Test
    fun `restart zeroes every counter`() {
        val finished = sampleSession(count = 10).copy(
            currentIndex = 10,
            correctCount = 8,
            skippedCount = 2,
            currentStreak = 3,
            longestStreak = 5,
            records = listOf(),
        )

        val result = useCase(finished)

        assertThat(result.currentIndex).isEqualTo(0)
        assertThat(result.correctCount).isEqualTo(0)
        assertThat(result.skippedCount).isEqualTo(0)
        assertThat(result.currentStreak).isEqualTo(0)
        assertThat(result.longestStreak).isEqualTo(0)
        assertThat(result.records).isEmpty()
    }

    @Test
    fun `restart keeps the same question list`() {
        val questions = sampleQuestions(count = 10)
        val finished = sampleSession(count = 10).copy(questions = questions, currentIndex = 10)

        val result = useCase(finished)

        assertThat(result.questions).isEqualTo(questions)
    }

    @Test
    fun `restart returns to the first question`() {
        val finished = sampleSession(count = 10).copy(currentIndex = 10)
        val result = useCase(finished)
        assertThat(result.currentQuestion).isEqualTo(result.questions.first())
    }
}
