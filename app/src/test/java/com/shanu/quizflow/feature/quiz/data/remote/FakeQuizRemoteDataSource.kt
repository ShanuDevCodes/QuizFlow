package com.shanu.quizflow.feature.quiz.data.remote

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.data.remote.dto.SubjectDto

class FakeQuizRemoteDataSource(
    private var questionsResult: () -> List<QuestionDto> = { emptyList() },
    private var subjectsResult: () -> List<SubjectDto> = { emptyList() },
) : QuizRemoteDataSource {

    var getSubjectsCallCount: Int = 0
        private set

    var getQuestionsCallCount: Int = 0
        private set

    fun setQuestionsResult(dtos: List<QuestionDto>) {
        questionsResult = { dtos }
    }

    fun setSubjectsResult(dtos: List<SubjectDto>) {
        subjectsResult = { dtos }
    }

    fun setFailure(throwable: Throwable) {
        questionsResult = { throw throwable }
        subjectsResult = { throw throwable }
    }

    override suspend fun getSubjects(): List<SubjectDto> {
        getSubjectsCallCount++
        return subjectsResult()
    }

    override suspend fun getQuestionsByUrl(url: String): List<QuestionDto> {
        getQuestionsCallCount++
        return questionsResult()
    }
}
