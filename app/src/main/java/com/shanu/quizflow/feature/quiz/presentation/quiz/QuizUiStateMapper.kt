package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.model.toResult
import javax.inject.Inject

class QuizUiStateMapper @Inject constructor() {

    operator fun invoke(session: QuizSession, phase: Phase, selectedIndex: Int?): QuizUiState {
        if (session.isFinished) return QuizUiState.Finished(session.toResult())

        val question = checkNotNull(session.currentQuestion) {
            "Cannot map to a Question state: quiz session has no current question."
        }
        val options = question.options.mapIndexed { index, text ->
            OptionUi(text = text, state = optionState(phase, selectedIndex, index, question.correctIndex))
        }

        return QuizUiState.Question(
            questionNumber = session.currentIndex + 1,
            totalQuestions = session.total,
            text = question.text,
            options = options,
            phase = phase,
            currentStreak = session.currentStreak,
            streakActive = session.isStreakActive,
        )
    }

    private fun optionState(phase: Phase, selectedIndex: Int?, index: Int, correctIndex: Int): OptionState = when {
        phase == Phase.ANSWERING -> OptionState.DEFAULT
        index == correctIndex -> OptionState.CORRECT
        index == selectedIndex -> OptionState.WRONG
        else -> OptionState.DIMMED
    }
}
