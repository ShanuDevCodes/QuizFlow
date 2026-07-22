package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.FakeQuizRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SyncSubjectsUseCaseTest {

    private val repository = FakeQuizRepository()
    private val useCase = SyncSubjectsUseCase(repository)

    @Test
    fun `invoke delegates syncSubjects to repository`() = runTest {
        val result = useCase()
        assertThat(result).isInstanceOf(DataResult.Success::class.java)
    }
}
