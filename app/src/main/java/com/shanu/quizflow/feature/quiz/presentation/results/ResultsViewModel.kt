package com.shanu.quizflow.feature.quiz.presentation.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.feature.quiz.domain.model.QuizResult
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val progressRepository: ModuleProgressRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var subjectId: String = savedStateHandle.get<String>("subjectId") ?: ""
        private set

    private val _resultState = MutableStateFlow<QuizResult?>(null)
    val resultState: StateFlow<QuizResult?> = _resultState.asStateFlow()

    init {
        if (subjectId.isNotEmpty()) {
            init(subjectId)
        }
    }

    fun init(targetSubjectId: String) {
        this.subjectId = targetSubjectId
        savedStateHandle["subjectId"] = targetSubjectId
        viewModelScope.launch {
            val progress = progressRepository.getProgress(targetSubjectId) ?: return@launch
            _resultState.value = QuizResult(
                correct = progress.lastScore,
                total = if (progress.totalQuestions > 0) progress.totalQuestions else 10,
                skipped = 0,
                longestStreak = progress.longestStreak,
                highScore = progress.highScore,
            )
        }
    }
}
