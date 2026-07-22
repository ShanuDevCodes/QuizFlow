package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeQuizRepository
import com.shanu.quizflow.feature.quiz.domain.model.Subject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetSubjectsUseCaseTest {

    private val repository = FakeQuizRepository()
    private val useCase = GetSubjectsUseCase(repository)

    @Test
    fun `invoke emits subjects flow from repository`() = runTest {
        val subjects = listOf(
            Subject(id = "sub1", title = "Basics", description = "Desc 1", questionsUrl = "http://q1"),
            Subject(id = "sub2", title = "Compose", description = "Desc 2", questionsUrl = "http://q2"),
        )
        repository.setSubjects(subjects)

        val result = useCase().first()

        assertThat(result).isEqualTo(subjects)
    }
}
