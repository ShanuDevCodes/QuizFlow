package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import javax.inject.Inject

class SaveModuleResultUseCase @Inject constructor(
    private val progressRepository: ModuleProgressRepository,
) {
    suspend operator fun invoke(subjectId: String, score: Int, total: Int, longestStreak: Int = 0) {
        progressRepository.saveResult(subjectId, score, total, longestStreak)
    }
}
