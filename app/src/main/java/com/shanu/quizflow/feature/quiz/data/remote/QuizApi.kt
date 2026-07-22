package com.shanu.quizflow.feature.quiz.data.remote

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.data.remote.dto.SubjectDto
import retrofit2.http.GET
import retrofit2.http.Url

interface QuizApi {
    @GET("dr-samrat/ee986f16da9d8303c1acfd364ece22c5/raw")
    suspend fun getSubjects(): List<SubjectDto>

    @GET
    suspend fun getQuestionsByUrl(@Url url: String): List<QuestionDto>
}
