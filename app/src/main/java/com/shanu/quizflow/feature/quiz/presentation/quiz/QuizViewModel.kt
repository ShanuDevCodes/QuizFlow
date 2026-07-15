package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.core.coroutines.DispatcherProvider
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.model.toResult
import com.shanu.quizflow.feature.quiz.domain.usecase.AdvanceQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.AnswerQuestionUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.GetQuestionsUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.RestartQuizUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SkipQuestionUseCase
import com.shanu.quizflow.feature.quiz.presentation.di.RevealDurationMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getQuestions: GetQuestionsUseCase,
    private val answerQuestion: AnswerQuestionUseCase,
    private val skipQuestion: SkipQuestionUseCase,
    private val advanceQuiz: AdvanceQuizUseCase,
    private val restartQuiz: RestartQuizUseCase,
    private val dispatcherProvider: DispatcherProvider,
    @param:RevealDurationMillis private val revealDurationMs: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private data class ScreenState(
        val session: QuizSession,
        val phase: Phase = Phase.ANSWERING,
        val selectedIndex: Int? = null,
    )

    private var screenState: ScreenState? = null
    private var revealJob: Job? = null

    init {
        loadQuestions()
    }

    fun onRetry() = loadQuestions()

    fun onOptionSelected(index: Int) {
        val current = screenState ?: return
        if (current.phase != Phase.ANSWERING) return

        val answered = answerQuestion(current.session, index)
        screenState = current.copy(
            session = answered.session,
            phase = Phase.REVEALING,
            selectedIndex = index,
        )
        publish()

        revealJob = viewModelScope.launch(dispatcherProvider.main) {
            delay(revealDurationMs)
            advanceToNext()
        }
    }

    fun onSkip() {
        val current = screenState ?: return
        if (current.phase != Phase.ANSWERING) return

        revealJob?.cancel()
        screenState = ScreenState(session = skipQuestion(current.session))
        publish()
    }

    fun onRestart() {
        val current = screenState ?: return
        revealJob?.cancel()
        screenState = ScreenState(session = restartQuiz(current.session))
        publish()
    }

    private fun advanceToNext() {
        val current = screenState ?: return
        screenState = ScreenState(session = advanceQuiz(current.session))
        publish()
    }

    private fun loadQuestions() {
        revealJob?.cancel()
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = QuizUiState.Loading
            when (val result = getQuestions()) {
                is DataResult.Success -> {
                    screenState = ScreenState(session = QuizSession(questions = result.data))
                    publish()
                }

                is DataResult.Error -> {
                    screenState = null
                    _uiState.value = QuizUiState.Error(result.error.toUserMessage())
                }
            }
        }
    }

    private fun publish() {
        val current = screenState ?: return
        _uiState.value = current.toUiState()
    }

    private fun ScreenState.toUiState(): QuizUiState {
        if (session.isFinished) return QuizUiState.Finished(session.toResult())

        val question = checkNotNull(session.currentQuestion)
        val options = question.options.mapIndexed { index, text ->
            OptionUi(text = text, state = optionState(index, question.correctIndex))
        }

        return QuizUiState.Question(
            questionNumber = session.currentIndex + 1,
            totalQuestions = session.total,
            text = question.text,
            options = options,
            phase = phase,
            currentStreak = session.currentStreak,
            streakActive = session.isStreakActive,
        )
    }

    private fun ScreenState.optionState(index: Int, correctIndex: Int): OptionState = when {
        phase == Phase.ANSWERING -> OptionState.DEFAULT
        index == correctIndex -> OptionState.CORRECT
        index == selectedIndex -> OptionState.WRONG
        else -> OptionState.DIMMED
    }

    override fun onCleared() {
        revealJob?.cancel()
        super.onCleared()
    }
}

private fun AppError.toUserMessage(): String = when (this) {
    AppError.Network -> "Couldn't reach the network. Check your connection and try again."
    AppError.ServerError -> "The server had a problem loading the quiz. Please try again."
    is AppError.Mapping -> "The quiz data looks invalid: $reason"
    is AppError.Unknown -> "Something went wrong. Please try again."
}
