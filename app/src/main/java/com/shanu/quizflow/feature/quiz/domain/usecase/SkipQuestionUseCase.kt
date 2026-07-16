package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.AnswerOutcome
import com.shanu.quizflow.feature.quiz.domain.model.AnswerRecord
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import javax.inject.Inject

class SkipQuestionUseCase @Inject constructor() {

    operator fun invoke(session: QuizSession): QuizSession {
        val question = checkNotNull(session.currentQuestion) {
            "Cannot skip: quiz session has no current question (already finished)."
        }

        val record = AnswerRecord(
            questionId = question.id,
            selectedIndex = null,
            outcome = AnswerOutcome.SKIPPED,
        )

        return session.copy(
            currentIndex = session.currentIndex + 1,
            skippedCount = session.skippedCount + 1,
            currentStreak = 0,
            records = session.records + record,
        )
    }
}
