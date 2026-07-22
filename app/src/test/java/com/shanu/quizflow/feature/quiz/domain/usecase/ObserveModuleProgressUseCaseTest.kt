package com.shanu.quizflow.feature.quiz.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeModuleProgressRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveModuleProgressUseCaseTest {

    private val repository = FakeModuleProgressRepository()
    private val useCase = ObserveModuleProgressUseCase(repository)

    @Test
    fun `invoke emits progress flow from repository`() = runTest {
        repository.saveResult("android_basics", score = 9, total = 10, longestStreak = 5)

        val progressList = useCase().first()

        assertThat(progressList).hasSize(1)
        assertThat(progressList[0].subjectId).isEqualTo("android_basics")
        assertThat(progressList[0].lastScore).isEqualTo(9)
        assertThat(progressList[0].longestStreak).isEqualTo(5)
    }
}
