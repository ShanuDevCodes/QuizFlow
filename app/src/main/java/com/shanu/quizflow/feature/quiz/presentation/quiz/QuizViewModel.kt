package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.core.coroutines.DispatcherProvider
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.model.toResult
import com.shanu.quizflow.feature.quiz.domain.usecase.AdvanceQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.AnswerQuestionUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.GetQuestionsUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.RestartQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.RestoreSessionUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SaveModuleResultUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SaveSessionStateUseCase
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
import kotlin.time.Duration.Companion.milliseconds

internal fun remainingLoadingDelayMs(minDurationMs: Long, elapsedMs: Long): Long =
    (minDurationMs - elapsedMs).coerceAtLeast(0L)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuestions: GetQuestionsUseCase,
    private val answerQuestion: AnswerQuestionUseCase,
    private val skipQuestion: SkipQuestionUseCase,
    private val advanceQuiz: AdvanceQuizUseCase,
    private val restartQuiz: RestartQuizUseCase,
    private val saveModuleResult: SaveModuleResultUseCase,
    private val saveSessionState: SaveSessionStateUseCase,
    private val restoreSession: RestoreSessionUseCase,
    private val uiStateMapper: QuizUiStateMapper,
    private val dispatcherProvider: DispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
    @param:RevealDurationMillis private val revealDurationMs: Long,
    @param:LoadingMinDurationMillis private val loadingMinDurationMs: Long,
) : ViewModel() {

    var subjectId: String = savedStateHandle.get<String>("subjectId") ?: ""
        private set

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var currentSession: QuizSession? = null
    private var currentPhase: Phase = Phase.ANSWERING
    private var selectedIndex: Int? = null
    private var revealJob: Job? = null
    private var saveSessionJob: Job? = null

    init {
        if (subjectId.isNotEmpty()) {
            loadQuestions(subjectId)
        }
    }

    fun init(subjectId: String, forceReload: Boolean = false) {
        if (!forceReload && this.subjectId == subjectId && _uiState.value !is QuizUiState.Loading) return
        this.subjectId = subjectId
        savedStateHandle["subjectId"] = subjectId
        loadQuestions(subjectId)
    }

    fun onRetry() {
        if (subjectId.isNotEmpty()) {
            loadQuestions(subjectId)
        }
    }

    fun onOptionSelected(index: Int) {
        val session = currentSession ?: return
        if (currentPhase != Phase.ANSWERING) return

        val answeredSession = answerQuestion(session, index)
        updateSession(answeredSession, phase = Phase.REVEALING, selectedIndex = index)

        revealJob = viewModelScope.launch(dispatcherProvider.main) {
            delay(revealDurationMs.milliseconds)
            advanceToNext()
        }
    }

    fun onSkip() {
        val session = currentSession ?: return
        if (currentPhase != Phase.ANSWERING) return

        revealJob?.cancel()
        updateSession(skipQuestion(session))
    }

    fun onRestart() {
        revealJob?.cancel()
        saveSessionJob?.cancel()
        val session = currentSession
        if (subjectId.isNotEmpty()) {
            viewModelScope.launch(dispatcherProvider.io) {
                restoreSession.clear(subjectId)
            }
        }
        if (session != null && session.questions.isNotEmpty()) {
            updateSession(restartQuiz(session))
        } else if (subjectId.isNotEmpty()) {
            loadQuestions(subjectId, isRestart = true)
        }
    }

    fun onFinish() {
        val session = currentSession ?: return
        revealJob?.cancel()
        updateSession(session.copy(currentIndex = session.questions.size))
    }

    private fun advanceToNext() {
        val session = currentSession ?: return
        updateSession(advanceQuiz(session))
    }

    private fun loadQuestions(targetSubjectId: String, isRestart: Boolean = false) {
        revealJob?.cancel()
        saveSessionJob?.cancel()
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = QuizUiState.Loading
            val startedAtMs = System.currentTimeMillis()

            if (!isRestart) {
                val restored = restoreSession(targetSubjectId)
                if (restored != null && !restored.isFinished) {
                    val elapsedMs = System.currentTimeMillis() - startedAtMs
                    delay(remainingLoadingDelayMs(loadingMinDurationMs, elapsedMs).milliseconds)
                    updateSession(restored)
                    return@launch
                }
            }

            val result = getQuestions(targetSubjectId)
            val elapsedMs = System.currentTimeMillis() - startedAtMs
            delay(remainingLoadingDelayMs(loadingMinDurationMs, elapsedMs).milliseconds)
            when (result) {
                is DataResult.Success -> updateSession(QuizSession(questions = result.data))
                is DataResult.Error -> {
                    currentSession = null
                    _uiState.value = QuizUiState.Error(result.error.toMessage())
                }
            }
        }
    }

    private fun updateSession(session: QuizSession, phase: Phase = Phase.ANSWERING, selectedIndex: Int? = null) {
        this.currentSession = session
        this.currentPhase = phase
        this.selectedIndex = selectedIndex

        saveSessionJob?.cancel()
        if (subjectId.isNotEmpty()) {
            if (session.isFinished) {
                val result = session.toResult()
                saveSessionJob = viewModelScope.launch(dispatcherProvider.main) {
                    restoreSession.clear(subjectId)
                    saveModuleResult(subjectId, result.correct, result.total, result.longestStreak)
                }
            } else {
                val isLastQuestion = session.currentIndex >= session.questions.size - 1
                if (!isLastQuestion) {
                    saveSessionJob = viewModelScope.launch(dispatcherProvider.io) {
                        saveSessionState(subjectId, session)
                    }
                }
            }
        }

        _uiState.value = uiStateMapper(session, phase, selectedIndex)
    }

    override fun onCleared() {
        revealJob?.cancel()
        saveSessionJob?.cancel()
        super.onCleared()
    }
}