package com.shanu.quizflow.feature.quiz.presentation.modulelist

import com.shanu.quizflow.feature.quiz.domain.model.ModuleStatus

data class OverallProgress(
    val completedModulesCount: Int = 0,
    val totalModulesCount: Int = 0,
    val completionPercentage: Int = 0,
    val totalScore: Int = 0,
    val averageScore: Float = 0f,
)

data class ModuleListUiState(
    val modules: List<ModuleUi> = emptyList(),
    val activeSessionModule: ModuleUi? = null,
    val overallProgress: OverallProgress = OverallProgress(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val syncError: String? = null,
)

data class ModuleUi(
    val id: String,
    val title: String,
    val description: String,
    val status: ModuleStatus,
    val isCompleted: Boolean = false,
    val totalQuestions: Int,
    val lastScore: Int?,
    val highScore: Int?,
)
