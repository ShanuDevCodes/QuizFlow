package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.AnswerOutcome
import com.shanu.quizflow.feature.quiz.domain.model.AnswerRecord
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import javax.inject.Inject

data class AnsweredQuestionResult(
    val session: QuizSession,
    val isCorrect: Boolean,
    val correctIndex: Int,
)

class AnswerQuestionUseCase @Inject constructor() {

    operator fun invoke(session: QuizSession, selectedIndex: Int): AnsweredQuestionResult {
        val question = checkNotNull(session.currentQuestion) {
            "Cannot answer: quiz session has no current question (already finished)."
        }

        val isCorrect = selectedIndex == question.correctIndex
        val newStreak = if (isCorrect) session.currentStreak + 1 else 0
        val record = AnswerRecord(
            questionId = question.id,
            selectedIndex = selectedIndex,
            outcome = if (isCorrect) AnswerOutcome.CORRECT else AnswerOutcome.WRONG,
        )

        val updatedSession = session.copy(
            correctCount = session.correctCount + if (isCorrect) 1 else 0,
            currentStreak = newStreak,
            longestStreak = maxOf(session.longestStreak, newStreak),
            records = session.records + record,
        )

        return AnsweredQuestionResult(
            session = updatedSession,
            isCorrect = isCorrect,
            correctIndex = question.correctIndex,
        )
    }
}
