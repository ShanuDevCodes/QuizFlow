package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import javax.inject.Inject

class AdvanceQuizUseCase @Inject constructor() {

    operator fun invoke(session: QuizSession): QuizSession =
        session.copy(currentIndex = session.currentIndex + 1)
}
