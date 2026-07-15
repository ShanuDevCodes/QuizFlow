package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.model.AnswerOutcome
import com.shanu.quizflow.feature.quiz.domain.model.AnswerRecord
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class SkipQuestionUseCaseTest {

    private val useCase = SkipQuestionUseCase()

    @Test
    fun `skip increments skippedCount`() {
        val result = useCase(sampleSession())
        assertThat(result.skippedCount).isEqualTo(1)
    }

    @Test
    fun `skip resets the current streak to 0`() {
        val session = sampleSession().copy(currentStreak = 5)
        val result = useCase(session)
        assertThat(result.currentStreak).isEqualTo(0)
    }

    @Test
    fun `skip does not affect longestStreak`() {
        val session = sampleSession().copy(currentStreak = 5, longestStreak = 5)
        val result = useCase(session)
        assertThat(result.longestStreak).isEqualTo(5)
    }

    @Test
    fun `skip advances immediately - no reveal delay`() {
        val session = sampleSession()
        val result = useCase(session)
        assertThat(result.currentIndex).isEqualTo(session.currentIndex + 1)
    }

    @Test
    fun `skip records a SKIPPED outcome with a null selected index`() {
        val session = sampleSession()
        val questionId = session.currentQuestion!!.id

        val result = useCase(session)

        assertThat(result.records).containsExactly(AnswerRecord(questionId, null, AnswerOutcome.SKIPPED))
    }

    @Test
    fun `skip does not affect correctCount`() {
        val session = sampleSession().copy(correctCount = 4)
        val result = useCase(session)
        assertThat(result.correctCount).isEqualTo(4)
    }

    @Test
    fun `skipping the last question throws when the session is already finished`() {
        val finished: QuizSession = sampleSession().copy(currentIndex = 10)

        try {
            useCase(finished)
            error("Expected IllegalStateException")
        } catch (expected: IllegalStateException) {
            // expected: no current question to skip
        }
    }
}
