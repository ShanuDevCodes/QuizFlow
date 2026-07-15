package com.shanu.quizflow.feature.quiz.domain.model

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
)
