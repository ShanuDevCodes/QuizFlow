package com.shanu.quizflow.feature.quiz.domain.usecase

import com.shanu.quizflow.feature.quiz.domain.model.Subject
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    operator fun invoke(): Flow<List<Subject>> = repository.observeSubjects()
}
