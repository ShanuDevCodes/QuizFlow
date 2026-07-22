package com.shanu.quizflow.feature.quiz.presentation.modulelist

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.FakeModuleProgressRepository
import com.shanu.quizflow.feature.quiz.domain.FakeQuizRepository
import com.shanu.quizflow.feature.quiz.domain.model.ModuleStatus
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.usecase.GetSubjectsUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.ObserveModuleProgressUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SyncSubjectsUseCase
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
class ModuleListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val quizRepository = FakeQuizRepository()
    private val progressRepository = FakeModuleProgressRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = ModuleListViewModel(
        getSubjects = GetSubjectsUseCase(quizRepository),
        observeProgress = ObserveModuleProgressUseCase(progressRepository),
        syncSubjects = SyncSubjectsUseCase(quizRepository),
        progressRepository = progressRepository,
    )

    @Test
    fun `loads subjects from repository into uiState`() = runTest(testDispatcher) {
        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.modules).hasSize(2)
        assertThat(state.modules[0].id).isEqualTo("android_basics")
        assertThat(state.modules[0].status).isEqualTo(ModuleStatus.NOT_STARTED)
    }

    @Test
    fun `reflects completed status after saving module result`() = runTest(testDispatcher) {
        progressRepository.saveResult("android_basics", 8, 10)

        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        val androidBasics = state.modules.first { it.id == "android_basics" }
        assertThat(androidBasics.status).isEqualTo(ModuleStatus.COMPLETED)
        assertThat(androidBasics.lastScore).isEqualTo(8)
    }

    @Test
    fun `identifies active session module when a session is in progress`() = runTest(testDispatcher) {
        val dummySession = QuizSession(
            questions = listOf(Question(id = 1, text = "Q1", options = listOf("A", "B", "C", "D"), correctIndex = 0)),
            currentIndex = 1,
            correctCount = 1,
        )
        progressRepository.saveSessionState("android_basics", dummySession)

        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        val androidBasics = state.modules.first { it.id == "android_basics" }
        assertThat(androidBasics.status).isEqualTo(ModuleStatus.IN_PROGRESS)
        assertThat(state.activeSessionModule).isNotNull()
        assertThat(state.activeSessionModule?.id).isEqualTo("android_basics")
    }

    @Test
    fun `computes overall progress correctly for completed modules`() = runTest(testDispatcher) {
        progressRepository.saveResult("android_basics", 10, 10)

        val vm = viewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val progress = vm.uiState.value.overallProgress
        assertThat(progress.completedModulesCount).isEqualTo(1)
        assertThat(progress.totalModulesCount).isEqualTo(2)
        assertThat(progress.completionPercentage).isEqualTo(50)
        assertThat(progress.totalScore).isEqualTo(10)
    }
}
