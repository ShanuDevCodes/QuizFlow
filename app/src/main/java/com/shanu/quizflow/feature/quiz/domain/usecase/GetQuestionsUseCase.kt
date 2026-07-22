package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    suspend operator fun invoke(subjectId: String): DataResult<List<Question>> =
        repository.getQuestions(subjectId)
}
