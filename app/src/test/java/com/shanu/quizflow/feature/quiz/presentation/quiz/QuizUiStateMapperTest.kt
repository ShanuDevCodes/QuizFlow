package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class QuizUiStateMapperTest {

    private val mapper = QuizUiStateMapper()

    @Test
    fun `maps an unfinished session to a Question state`() {
        val session = sampleSession()

        val state = mapper(session, phase = Phase.ANSWERING, selectedIndex = null) as QuizUiState.Question

        assertThat(state.questionNumber).isEqualTo(1)
        assertThat(state.totalQuestions).isEqualTo(session.total)
        assertThat(state.options).hasSize(4)
        assertThat(state.currentStreak).isEqualTo(session.currentStreak)
        assertThat(state.streakActive).isEqualTo(session.isStreakActive)
    }

    @Test
    fun `ANSWERING phase leaves every option DEFAULT regardless of correctness`() {
        val session = sampleSession() // correctIndex is 0 for all fixture questions

        val state = mapper(session, phase = Phase.ANSWERING, selectedIndex = 0) as QuizUiState.Question

        assertThat(state.options.map { it.state }).containsExactly(
            OptionState.DEFAULT, OptionState.DEFAULT, OptionState.DEFAULT, OptionState.DEFAULT,
        )
    }

    @Test
    fun `REVEALING phase marks the correct option CORRECT and the wrong selection WRONG`() {
        val session = sampleSession() // correctIndex is 0

        val state = mapper(session, phase = Phase.REVEALING, selectedIndex = 2) as QuizUiState.Question

        assertThat(state.options[0].state).isEqualTo(OptionState.CORRECT)
        assertThat(state.options[2].state).isEqualTo(OptionState.WRONG)
        assertThat(state.options[1].state).isEqualTo(OptionState.DIMMED)
        assertThat(state.options[3].state).isEqualTo(OptionState.DIMMED)
    }

    @Test
    fun `REVEALING phase after a correct selection only marks CORRECT, nothing WRONG`() {
        val session = sampleSession() // correctIndex is 0

        val state = mapper(session, phase = Phase.REVEALING, selectedIndex = 0) as QuizUiState.Question

        assertThat(state.options[0].state).isEqualTo(OptionState.CORRECT)
        assertThat(state.options.drop(1).map { it.state }).containsExactly(
            OptionState.DIMMED, OptionState.DIMMED, OptionState.DIMMED,
        )
    }

    @Test
    fun `maps a finished session to a Finished state with the session's tallies`() {
        val session = sampleSession(count = 1).copy(currentIndex = 1, correctCount = 1)

        val state = mapper(session, phase = Phase.ANSWERING, selectedIndex = null)

        assertThat(state).isInstanceOf(QuizUiState.Finished::class.java)
        assertThat((state as QuizUiState.Finished).result.correct).isEqualTo(1)
        assertThat(state.result.total).isEqualTo(1)
    }
}
