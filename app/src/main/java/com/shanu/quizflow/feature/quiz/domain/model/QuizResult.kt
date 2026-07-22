package com.shanu.quizflow.feature.quiz.domain.model

data class QuizResult(
    val correct: Int,
    val total: Int,
    val skipped: Int,
    val longestStreak: Int,
    val highScore: Int? = null,
)

fun QuizSession.toResult(highScore: Int? = null): QuizResult = QuizResult(
    correct = correctCount,
    total = total,
    skipped = skippedCount,
    longestStreak = longestStreak,
    highScore = highScore,
)
