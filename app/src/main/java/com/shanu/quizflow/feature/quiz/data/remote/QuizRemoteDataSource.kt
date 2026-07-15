package com.shanu.quizflow.feature.quiz.data.remote

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import javax.inject.Inject

interface QuizRemoteDataSource {
    suspend fun getQuestions(): List<QuestionDto>
}

class QuizRemoteDataSourceImpl @Inject constructor(
    private val api: QuizApi,
) : QuizRemoteDataSource {
    override suspend fun getQuestions(): List<QuestionDto> = api.getQuestions()
}
