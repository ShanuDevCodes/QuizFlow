package com.shanu.quizflow.feature.quiz.domain

import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository

class FakeQuizRepository(
    private var result: DataResult<List<Question>> = DataResult.Success(sampleQuestions()),
) : QuizRepository {

    var getQuestionsCallCount: Int = 0
        private set

    fun setResult(result: DataResult<List<Question>>) {
        this.result = result
    }

    override suspend fun getQuestions(): DataResult<List<Question>> {
        getQuestionsCallCount++
        return result
    }
}

fun errorResult(error: AppError = AppError.Network): DataResult<List<Question>> = DataResult.Error(error)
