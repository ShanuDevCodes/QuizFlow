package com.shanu.quizflow.feature.quiz.data.remote

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto

class FakeQuizRemoteDataSource(
    private var result: () -> List<QuestionDto> = { emptyList() },
) : QuizRemoteDataSource {

    var callCount: Int = 0
        private set

    fun setResult(dtos: List<QuestionDto>) {
        result = { dtos }
    }

    fun setFailure(throwable: Throwable) {
        result = { throw throwable }
    }

    override suspend fun getQuestions(): List<QuestionDto> {
        callCount++
        return result()
    }
}
