package com.shanu.quizflow.feature.quiz.domain.repository

import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun observeSubjects(): Flow<List<Subject>>
    suspend fun syncSubjects(): DataResult<Unit>
    suspend fun getQuestions(subjectId: String): DataResult<List<Question>>
}
