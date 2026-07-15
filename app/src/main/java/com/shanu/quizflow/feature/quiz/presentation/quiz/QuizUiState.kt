package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.shanu.quizflow.feature.quiz.domain.model.QuizResult

sealed interface QuizUiState {
    data object Loading : QuizUiState

    data class Error(val message: String) : QuizUiState

    data class Question(
        val questionNumber: Int,
        val totalQuestions: Int,
        val text: String,
        val options: List<OptionUi>,
        val phase: Phase,
        val currentStreak: Int,
        val streakActive: Boolean,
    ) : QuizUiState

    data class Finished(val result: QuizResult) : QuizUiState
}

data class OptionUi(val text: String, val state: OptionState)

enum class OptionState { DEFAULT, CORRECT, WRONG, DIMMED }

enum class Phase { ANSWERING, REVEALING }
