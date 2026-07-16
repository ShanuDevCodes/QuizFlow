package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.core.coroutines.DispatcherProvider
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.usecase.AdvanceQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.AnswerQuestionUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.GetQuestionsUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.RestartQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SkipQuestionUseCase
import com.shanu.quizflow.feature.quiz.presentation.di.LoadingMinDurationMillis
import com.shanu.quizflow.feature.quiz.presentation.di.RevealDurationMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal fun remainingLoadingDelayMs(minDurationMs: Long, elapsedMs: Long): Long =
    (minDurationMs - elapsedMs).coerceAtLeast(0L)

private sealed interface InternalState {
    data object Loading : InternalState
    data class Error(val message: AppErrorMessage) : InternalState
    data class Active(
        val session: QuizSession,
        val phase: Phase = Phase.ANSWERING,
        val selectedIndex: Int? = null,
    ) : InternalState
}

private const val KeyCurrentIndex = "quiz_current_index"
private const val KeyCorrectCount = "quiz_correct_count"
private const val KeySkippedCount = "quiz_skipped_count"
private const val KeyCurrentStreak = "quiz_current_streak"
private const val KeyLongestStreak = "quiz_longest_streak"

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuestions: GetQuestionsUseCase,
    private val answerQuestion: AnswerQuestionUseCase,
    private val skipQuestion: SkipQuestionUseCase,
    private val advanceQuiz: AdvanceQuizUseCase,
    private val restartQuiz: RestartQuizUseCase,
    private val uiStateMapper: QuizUiStateMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
    @param:RevealDurationMillis private val revealDurationMs: Long,
    @param:LoadingMinDurationMillis private val loadingMinDurationMs: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var internalState: InternalState = InternalState.Loading
        set(value) {
            field = value
            if (value is InternalState.Active) persistProgress(value.session)
            _uiState.value = value.toUiState()
        }

    private var revealJob: Job? = null

    init {
        loadQuestions()
    }

    fun onRetry() = loadQuestions()

    fun onOptionSelected(index: Int) {
        val current = internalState as? InternalState.Active ?: return
        if (current.phase != Phase.ANSWERING) return

        val answeredSession = answerQuestion(current.session, index)
        internalState = current.copy(
            session = answeredSession,
            phase = Phase.REVEALING,
            selectedIndex = index,
        )

        revealJob = viewModelScope.launch(dispatcherProvider.main) {
            delay(revealDurationMs)
            advanceToNext()
        }
    }

    fun onSkip() {
        val current = internalState as? InternalState.Active ?: return
        if (current.phase != Phase.ANSWERING) return

        revealJob?.cancel()
        internalState = InternalState.Active(session = skipQuestion(current.session))
    }

    fun onRestart() {
        val current = internalState as? InternalState.Active ?: return
        revealJob?.cancel()
        internalState = InternalState.Active(session = restartQuiz(current.session))
    }

    private fun advanceToNext() {
        val current = internalState as? InternalState.Active ?: return
        internalState = InternalState.Active(session = advanceQuiz(current.session))
    }

    private fun loadQuestions() {
        revealJob?.cancel()
        viewModelScope.launch(dispatcherProvider.main) {
            internalState = InternalState.Loading
            val startedAtMs = System.currentTimeMillis()
            val result = getQuestions()
            val elapsedMs = System.currentTimeMillis() - startedAtMs
            delay(remainingLoadingDelayMs(loadingMinDurationMs, elapsedMs))
            internalState = when (result) {
                is DataResult.Success -> InternalState.Active(session = restoreOrCreateSession(result.data))
                is DataResult.Error -> InternalState.Error(result.error.toMessage())
            }
        }
    }

    private fun restoreOrCreateSession(questions: List<Question>): QuizSession {
        val savedIndex = savedStateHandle.get<Int>(KeyCurrentIndex) ?: return QuizSession(questions = questions)
        return QuizSession(
            questions = questions,
            currentIndex = savedIndex,
            correctCount = savedStateHandle.get<Int>(KeyCorrectCount) ?: 0,
            skippedCount = savedStateHandle.get<Int>(KeySkippedCount) ?: 0,
            currentStreak = savedStateHandle.get<Int>(KeyCurrentStreak) ?: 0,
            longestStreak = savedStateHandle.get<Int>(KeyLongestStreak) ?: 0,
        )
    }

    private fun persistProgress(session: QuizSession) {
        savedStateHandle[KeyCurrentIndex] = session.currentIndex
        savedStateHandle[KeyCorrectCount] = session.correctCount
        savedStateHandle[KeySkippedCount] = session.skippedCount
        savedStateHandle[KeyCurrentStreak] = session.currentStreak
        savedStateHandle[KeyLongestStreak] = session.longestStreak
    }

    private fun InternalState.toUiState(): QuizUiState = when (this) {
        InternalState.Loading -> QuizUiState.Loading
        is InternalState.Error -> QuizUiState.Error(message)
        is InternalState.Active -> uiStateMapper(session, phase, selectedIndex)
    }

    override fun onCleared() {
        revealJob?.cancel()
        super.onCleared()
    }
}
