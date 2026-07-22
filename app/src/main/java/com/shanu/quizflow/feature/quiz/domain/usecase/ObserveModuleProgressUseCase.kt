package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.ModuleProgress
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveModuleProgressUseCase @Inject constructor(
    private val repository: ModuleProgressRepository,
) {
    operator fun invoke(): Flow<List<ModuleProgress>> = repository.observeAllProgress()
}
