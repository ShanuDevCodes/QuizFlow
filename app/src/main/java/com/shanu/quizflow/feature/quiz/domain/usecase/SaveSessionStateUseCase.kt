package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import javax.inject.Inject

class SaveSessionStateUseCase @Inject constructor(
    private val repository: ModuleProgressRepository,
) {
    suspend operator fun invoke(subjectId: String, session: QuizSession) {
        repository.saveSessionState(subjectId, session)
    }
}
