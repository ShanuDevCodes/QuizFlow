package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeModuleProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResultsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeModuleProgressRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads result state when subjectId is present in SavedStateHandle`() = runTest(testDispatcher) {
        repository.saveResult("android_basics", score = 8, total = 10, longestStreak = 4)
        val handle = SavedStateHandle(mapOf("subjectId" to "android_basics"))

        val viewModel = ResultsViewModel(repository, handle)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.subjectId).isEqualTo("android_basics")
        val state = viewModel.resultState.value
        assertThat(state).isNotNull()
        assertThat(state?.correct).isEqualTo(8)
        assertThat(state?.total).isEqualTo(10)
        assertThat(state?.longestStreak).isEqualTo(4)
    }

    @Test
    fun `init with targetSubjectId updates subjectId and loads result state`() = runTest(testDispatcher) {
        repository.saveResult("security", score = 9, total = 10, longestStreak = 5)
        val handle = SavedStateHandle()

        val viewModel = ResultsViewModel(repository, handle)
        viewModel.init("security")
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.subjectId).isEqualTo("security")
        assertThat(handle.get<String>("subjectId")).isEqualTo("security")
        val state = viewModel.resultState.value
        assertThat(state).isNotNull()
        assertThat(state?.correct).isEqualTo(9)
        assertThat(state?.total).isEqualTo(10)
    }

    @Test
    fun `resultState remains null when subject progress does not exist`() = runTest(testDispatcher) {
        val handle = SavedStateHandle()

        val viewModel = ResultsViewModel(repository, handle)
        viewModel.init("non_existent")
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.resultState.value).isNull()
    }
}
