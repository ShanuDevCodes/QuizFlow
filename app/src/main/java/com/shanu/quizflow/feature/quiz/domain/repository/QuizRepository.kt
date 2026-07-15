package com.shanu.quizflow.feature.quiz.domain.repository

import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.Question

interface QuizRepository {
    suspend fun getQuestions(): DataResult<List<Question>>
}
