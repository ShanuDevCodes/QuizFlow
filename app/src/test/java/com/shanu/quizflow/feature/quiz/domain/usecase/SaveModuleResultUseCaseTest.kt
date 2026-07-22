package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeModuleProgressRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveModuleResultUseCaseTest {

    private val repository = FakeModuleProgressRepository()
    private val useCase = SaveModuleResultUseCase(repository)

    @Test
    fun `invoke delegates saveResult to repository`() = runTest {
        useCase("android_basics", score = 10, total = 10, longestStreak = 6)

        val progress = repository.getProgress("android_basics")
        assertThat(progress).isNotNull()
        assertThat(progress?.lastScore).isEqualTo(10)
        assertThat(progress?.totalQuestions).isEqualTo(10)
        assertThat(progress?.longestStreak).isEqualTo(6)
        assertThat(progress?.isCompleted).isTrue()
    }
}
