package com.shanu.quizflow.feature.quiz.domain.model

/**
 * Single source of truth for one quiz attempt. Immutable; every use case returns a new copy
 * rather than mutating in place, so the ViewModel layer stays trivially testable.
 */
data class QuizSession(
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val skippedCount: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val records: List<AnswerRecord> = emptyList(),
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val total: Int get() = questions.size
    val isFinished: Boolean get() = currentIndex >= questions.size

    /** Streak badge "lights up" once the user has 3+ consecutive correct answers. */
    val isStreakActive: Boolean get() = currentStreak >= STREAK_BADGE_THRESHOLD

    companion object {
        const val STREAK_BADGE_THRESHOLD = 3
    }
}
