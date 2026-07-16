package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.model.AnswerOutcome
import com.shanu.quizflow.feature.quiz.domain.model.AnswerRecord
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class AnswerQuestionUseCaseTest {

    private val useCase = AnswerQuestionUseCase()

    @Test
    fun `correct answer increments correctCount and streak`() {
        val session = sampleSession()

        val result = useCase(session, selectedIndex = 0) // correctIndex is 0 for all fixture questions

        assertThat(result.isCorrect).isTrue()
        assertThat(result.correctIndex).isEqualTo(0)
        assertThat(result.session.correctCount).isEqualTo(1)
        assertThat(result.session.currentStreak).isEqualTo(1)
        assertThat(result.session.longestStreak).isEqualTo(1)
    }

    @Test
    fun `wrong answer does not increment correctCount and resets streak to 0`() {
        val session = sampleSession().copy(currentStreak = 5, correctCount = 3)

        val result = useCase(session, selectedIndex = 1) // wrong: correctIndex is 0

        assertThat(result.isCorrect).isFalse()
        assertThat(result.session.correctCount).isEqualTo(3)
        assertThat(result.session.currentStreak).isEqualTo(0)
    }

    @Test
    fun `longestStreak is preserved after a wrong answer resets the current streak`() {
        val session = sampleSession().copy(currentStreak = 5, longestStreak = 5)

        val result = useCase(session, selectedIndex = 1) // wrong

        assertThat(result.session.currentStreak).isEqualTo(0)
        assertThat(result.session.longestStreak).isEqualTo(5)
    }

    @Test
    fun `longestStreak updates when the current streak exceeds it`() {
        val session = sampleSession().copy(currentStreak = 2, longestStreak = 2)

        val result = useCase(session, selectedIndex = 0) // correct -> streak becomes 3

        assertThat(result.session.currentStreak).isEqualTo(3)
        assertThat(result.session.longestStreak).isEqualTo(3)
    }

    @Test
    fun `streak badge activates at exactly 3 consecutive correct answers`() {
        var session = sampleSession()
        repeat(2) { session = useCase(session, selectedIndex = 0).session }
        assertThat(session.isStreakActive).isFalse()

        session = useCase(session, selectedIndex = 0).session

        assertThat(session.currentStreak).isEqualTo(3)
        assertThat(session.isStreakActive).isTrue()
    }

    @Test
    fun `answering does not advance currentIndex - reveal happens first`() {
        val session = sampleSession()

        val result = useCase(session, selectedIndex = 0)

        assertThat(result.session.currentIndex).isEqualTo(session.currentIndex)
    }

    @Test
    fun `answering records the outcome with the selected index`() {
        val session = sampleSession()
        val questionId = session.currentQuestion!!.id

        val correct = useCase(session, selectedIndex = 0)
        assertThat(correct.session.records).containsExactly(
            AnswerRecord(questionId, 0, AnswerOutcome.CORRECT),
        )

        val wrong = useCase(session, selectedIndex = 2)
        assertThat(wrong.session.records).containsExactly(
            AnswerRecord(questionId, 2, AnswerOutcome.WRONG),
        )
    }

    @Test
    fun `answering the last question throws when the session is already finished`() {
        val finished: QuizSession = sampleSession().copy(currentIndex = 10)

        try {
            useCase(finished, selectedIndex = 0)
            error("Expected IllegalStateException")
        } catch (expected: IllegalStateException) {
        }
    }

    @Test
    fun `answer sequence C,C,C,W,C,C,C,C keeps longest streak at 4`() {
        val outcomes = listOf(true, true, true, false, true, true, true, true)
        var session = sampleSession(count = outcomes.size)

        for (isCorrect in outcomes) {
            val selected = if (isCorrect) 0 else 1
            val result = useCase(session, selectedIndex = selected)
            session = result.session.copy(currentIndex = session.currentIndex + 1)
        }

        assertThat(session.correctCount).isEqualTo(7)
        assertThat(session.currentStreak).isEqualTo(4)
        assertThat(session.longestStreak).isEqualTo(4)
    }
}
