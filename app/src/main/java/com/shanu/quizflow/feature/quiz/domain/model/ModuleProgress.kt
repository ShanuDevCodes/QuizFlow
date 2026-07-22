package com.shanu.quizflow.feature.quiz.domain.model

enum class ModuleStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}

data class ModuleProgress(
    val subjectId: String,
    val isCompleted: Boolean = false,
    val lastScore: Int = 0,
    val highScore: Int = 0,
    val longestStreak: Int = 0,
    val totalQuestions: Int = 0,
)
