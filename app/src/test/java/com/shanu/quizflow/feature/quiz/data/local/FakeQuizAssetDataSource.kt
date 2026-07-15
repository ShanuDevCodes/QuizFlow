package com.shanu.quizflow.feature.quiz.data.local

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto

class FakeQuizAssetDataSource(
    private var result: () -> List<QuestionDto> = { emptyList() },
) : QuizAssetDataSource {

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
