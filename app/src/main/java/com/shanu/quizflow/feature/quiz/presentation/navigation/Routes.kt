package com.shanu.quizflow.feature.quiz.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LoadingRoute : NavKey

@Serializable
data object QuizRoute : NavKey

@Serializable
data object ResultsRoute : NavKey
