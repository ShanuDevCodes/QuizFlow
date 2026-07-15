package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import javax.inject.Inject

/** Resets all counters and returns to the first question, reusing the same question list. */
class RestartQuizUseCase @Inject constructor() {

    operator fun invoke(session: QuizSession): QuizSession = QuizSession(questions = session.questions)
}
