package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeModuleProgressRepository
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.sampleQuestions
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RestoreSessionUseCaseTest {

    private val repository = FakeModuleProgressRepository()
    private val useCase = RestoreSessionUseCase(repository)

    @Test
    fun `invoke returns null when no session saved`() = runTest {
        val result = useCase("android_basics")
        assertThat(result).isNull()
    }

    @Test
    fun `invoke returns saved QuizSession when present`() = runTest {
        val questions = sampleQuestions(count = 3)
        val session = QuizSession(questions = questions, currentIndex = 1)
        repository.saveSessionState("android_basics", session)

        val result = useCase("android_basics")

        assertThat(result).isNotNull()
        assertThat(result?.currentIndex).isEqualTo(1)
        assertThat(result?.questions).hasSize(3)
    }
}
