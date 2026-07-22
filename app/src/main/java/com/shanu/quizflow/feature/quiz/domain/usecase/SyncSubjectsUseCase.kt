package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import javax.inject.Inject

class SyncSubjectsUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    suspend operator fun invoke(): DataResult<Unit> = repository.syncSubjects()
}
