package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeModuleProgressRepository
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.sampleQuestions
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveSessionStateUseCaseTest {

    private val repository = FakeModuleProgressRepository()
    private val useCase = SaveSessionStateUseCase(repository)

    @Test
    fun `invoke delegates saveSessionState to repository`() = runTest {
        val session = QuizSession(questions = sampleQuestions(count = 5), currentIndex = 2)

        useCase("android_basics", session)

        val restored = repository.getSessionState("android_basics")
        assertThat(restored).isNotNull()
        assertThat(restored?.currentIndex).isEqualTo(2)
        assertThat(restored?.questions).hasSize(5)
    }
}
