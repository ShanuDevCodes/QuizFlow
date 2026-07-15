package com.shanu.quizflow.feature.quiz.domain.model

enum class AnswerOutcome { CORRECT, WRONG, SKIPPED }

data class AnswerRecord(
    val questionId: Int,
    val selectedIndex: Int?,
    val outcome: AnswerOutcome,
)
