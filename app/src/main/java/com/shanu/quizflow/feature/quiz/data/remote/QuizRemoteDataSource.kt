package com.shanu.quizflow.feature.quiz.data.remote

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.data.remote.dto.SubjectDto
import javax.inject.Inject

interface QuizRemoteDataSource {
    suspend fun getSubjects(): List<SubjectDto>
    suspend fun getQuestionsByUrl(url: String): List<QuestionDto>
}

class QuizRemoteDataSourceImpl @Inject constructor(
    private val api: QuizApi,
) : QuizRemoteDataSource {
    override suspend fun getSubjects(): List<SubjectDto> = api.getSubjects()
    override suspend fun getQuestionsByUrl(url: String): List<QuestionDto> = api.getQuestionsByUrl(url)
}
