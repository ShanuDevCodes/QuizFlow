package com.shanu.quizflow.feature.quiz.domain.model

data class QuizResult(
    val correct: Int,
    val total: Int,
    val skipped: Int,
    val longestStreak: Int,
)

fun QuizSession.toResult(): QuizResult = QuizResult(
    correct = correctCount,
    total = total,
    skipped = skippedCount,
    longestStreak = longestStreak,
)
