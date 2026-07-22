package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import javax.inject.Inject

class RestoreSessionUseCase @Inject constructor(
    private val repository: ModuleProgressRepository,
) {
    suspend operator fun invoke(subjectId: String): QuizSession? =
        repository.getSessionState(subjectId)

    suspend fun clear(subjectId: String) =
        repository.clearSessionState(subjectId)
}
