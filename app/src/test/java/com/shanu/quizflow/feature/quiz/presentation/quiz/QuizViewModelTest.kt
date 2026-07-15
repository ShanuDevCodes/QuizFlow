package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.coroutines.FakeDispatcherProvider
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.FakeQuizRepository
import com.shanu.quizflow.feature.quiz.domain.errorResult
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import com.shanu.quizflow.feature.quiz.domain.sampleQuestions
import com.shanu.quizflow.feature.quiz.domain.usecase.AdvanceQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.AnswerQuestionUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.GetQuestionsUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.RestartQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SkipQuestionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val REVEAL_MS = 2_000L

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        repository: QuizRepository = FakeQuizRepository(),
        revealMs: Long = REVEAL_MS,
    ) = QuizViewModel(
        getQuestions = GetQuestionsUseCase(repository),
        answerQuestion = AnswerQuestionUseCase(),
        skipQuestion = SkipQuestionUseCase(),
        advanceQuiz = AdvanceQuizUseCase(),
        restartQuiz = RestartQuizUseCase(),
        dispatcherProvider = FakeDispatcherProvider(testDispatcher),
        revealDurationMs = revealMs,
    )

    @Test
    fun `shows Loading synchronously before the load coroutine runs`() = runTest(testDispatcher) {
        val vm = viewModel()
        assertThat(vm.uiState.value).isEqualTo(QuizUiState.Loading)
    }

    @Test
    fun `loads successfully into a Question state for question 1`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.questionNumber).isEqualTo(1)
        assertThat(state.totalQuestions).isEqualTo(10)
        assertThat(state.phase).isEqualTo(Phase.ANSWERING)
        assertThat(state.options).hasSize(4)
        assertThat(state.options.all { it.state == OptionState.DEFAULT }).isTrue()
    }

    @Test
    fun `load failure surfaces an Error state with a message`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(errorResult(AppError.Network)))
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(vm.uiState.value).isInstanceOf(QuizUiState.Error::class.java)
        assertThat((vm.uiState.value as QuizUiState.Error).message).isNotEmpty()
    }

    @Test
    fun `onRetry re-attempts loading and can succeed after a prior failure`() = runTest(testDispatcher) {
        val repository = FakeQuizRepository(errorResult(AppError.Network))
        val vm = viewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(vm.uiState.value).isInstanceOf(QuizUiState.Error::class.java)

        repository.setResult(DataResult.Success(sampleQuestions(10)))
        vm.onRetry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(vm.uiState.value).isInstanceOf(QuizUiState.Question::class.java)
    }

    @Test
    fun `selecting the correct option reveals CORRECT and dims the rest`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(0) // fixture questions have correctIndex = 0

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.phase).isEqualTo(Phase.REVEALING)
        assertThat(state.options[0].state).isEqualTo(OptionState.CORRECT)
        assertThat(state.options[1].state).isEqualTo(OptionState.DIMMED)
        assertThat(state.currentStreak).isEqualTo(1)
    }

    @Test
    fun `selecting a wrong option reveals both CORRECT and WRONG`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(2) // wrong: correctIndex is 0

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.options[0].state).isEqualTo(OptionState.CORRECT)
        assertThat(state.options[2].state).isEqualTo(OptionState.WRONG)
        assertThat(state.options[1].state).isEqualTo(OptionState.DIMMED)
        assertThat(state.options[3].state).isEqualTo(OptionState.DIMMED)
        assertThat(state.currentStreak).isEqualTo(0)
    }

    @Test
    fun `a second option tap during REVEALING is ignored`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(0)
        val afterFirstTap = vm.uiState.value as QuizUiState.Question
        vm.onOptionSelected(1) // should be ignored: already REVEALING

        assertThat(vm.uiState.value).isEqualTo(afterFirstTap)
    }

    @Test
    fun `state stays REVEALING before the reveal duration elapses`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(0)
        testDispatcher.scheduler.advanceTimeBy(REVEAL_MS - 1)

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.phase).isEqualTo(Phase.REVEALING)
        assertThat(state.questionNumber).isEqualTo(1)
    }

    @Test
    fun `auto-advances to the next question once the reveal duration elapses`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(0)
        testDispatcher.scheduler.advanceTimeBy(REVEAL_MS)
        testDispatcher.scheduler.runCurrent()

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.questionNumber).isEqualTo(2)
        assertThat(state.phase).isEqualTo(Phase.ANSWERING)
    }

    @Test
    fun `auto-advancing past the last question transitions to Finished`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(1))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(0)
        testDispatcher.scheduler.advanceTimeBy(REVEAL_MS)
        testDispatcher.scheduler.runCurrent()

        val state = vm.uiState.value as QuizUiState.Finished
        assertThat(state.result.correct).isEqualTo(1)
        assertThat(state.result.total).isEqualTo(1)
    }

    @Test
    fun `skip advances immediately without waiting for the reveal duration`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onSkip()

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.questionNumber).isEqualTo(2)
        assertThat(state.phase).isEqualTo(Phase.ANSWERING)
    }

    @Test
    fun `skip resets the current streak to 0`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()
        vm.onOptionSelected(0) // correct -> streak 1, but still revealing
        testDispatcher.scheduler.advanceTimeBy(REVEAL_MS)
        testDispatcher.scheduler.runCurrent() // now on question 2, streak = 1

        vm.onSkip()

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.currentStreak).isEqualTo(0)
    }

    @Test
    fun `skip is ignored while REVEALING`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onOptionSelected(0)
        val revealingState = vm.uiState.value
        vm.onSkip()

        assertThat(vm.uiState.value).isEqualTo(revealingState)
    }

    @Test
    fun `streakActive becomes true after 3 consecutive correct answers`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(10))))
        testDispatcher.scheduler.advanceUntilIdle()

        repeat(3) {
            vm.onOptionSelected(0)
            testDispatcher.scheduler.advanceTimeBy(REVEAL_MS)
            testDispatcher.scheduler.runCurrent()
        }

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.questionNumber).isEqualTo(4)
        assertThat(state.streakActive).isTrue()
    }

    @Test
    fun `restart from Finished returns to question 1 with a zeroed session`() = runTest(testDispatcher) {
        val vm = viewModel(FakeQuizRepository(DataResult.Success(sampleQuestions(1))))
        testDispatcher.scheduler.advanceUntilIdle()
        vm.onOptionSelected(0)
        testDispatcher.scheduler.advanceTimeBy(REVEAL_MS)
        testDispatcher.scheduler.runCurrent()
        assertThat(vm.uiState.value).isInstanceOf(QuizUiState.Finished::class.java)

        vm.onRestart()

        val state = vm.uiState.value as QuizUiState.Question
        assertThat(state.questionNumber).isEqualTo(1)
        assertThat(state.currentStreak).isEqualTo(0)
        assertThat(state.phase).isEqualTo(Phase.ANSWERING)
    }
}
