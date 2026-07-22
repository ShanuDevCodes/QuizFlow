package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.FakeQuizRepository
import com.shanu.quizflow.feature.quiz.domain.errorResult
import com.shanu.quizflow.feature.quiz.domain.sampleQuestions
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetQuestionsUseCaseTest {

    @Test
    fun `invoke returns the repository's success result`() = runTest {
        val questions = sampleQuestions()
        val repository = FakeQuizRepository(DataResult.Success(questions))
        val useCase = GetQuestionsUseCase(repository)

        val result = useCase("android_basics")

        assertThat(result).isEqualTo(DataResult.Success(questions))
    }

    @Test
    fun `invoke propagates the repository's error result`() = runTest {
        val repository = FakeQuizRepository(errorResult(AppError.Network))
        val useCase = GetQuestionsUseCase(repository)

        val result = useCase("android_basics")

        assertThat(result).isEqualTo(DataResult.Error(AppError.Network))
    }

    @Test
    fun `invoke delegates exactly once to the repository`() = runTest {
        val repository = FakeQuizRepository()
        val useCase = GetQuestionsUseCase(repository)

        useCase("android_basics")

        assertThat(repository.getQuestionsCallCount).isEqualTo(1)
    }
}
