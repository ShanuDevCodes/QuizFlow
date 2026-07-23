package com.shanu.quizflow.feature.quiz.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ModuleListRoute : NavKey

@Serializable
data class QuizRoute(
    val subjectId: String,
    val instanceId: Long = System.nanoTime(),
) : NavKey

@Serializable
data class ResultsRoute(
    val subjectId: String,
    val correct: Int = 0,
    val total: Int = 0,
    val skipped: Int = 0,
    val streak: Int = 0,
    val isReview: Boolean = false,
) : NavKey
