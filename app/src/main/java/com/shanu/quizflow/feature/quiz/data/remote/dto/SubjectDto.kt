package com.shanu.quizflow.feature.quiz.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
    val id: String,
    val title: String,
    val description: String,
    @SerialName("questions_url")
    val questionsUrl: String,
)
