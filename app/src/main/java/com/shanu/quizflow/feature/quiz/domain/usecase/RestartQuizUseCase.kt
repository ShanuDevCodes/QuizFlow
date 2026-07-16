package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import javax.inject.Inject

class RestartQuizUseCase @Inject constructor() {

    operator fun invoke(session: QuizSession): QuizSession = QuizSession(questions = session.questions)
}
