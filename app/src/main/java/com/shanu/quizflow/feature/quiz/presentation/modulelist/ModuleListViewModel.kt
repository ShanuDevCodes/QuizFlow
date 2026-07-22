package com.shanu.quizflow.feature.quiz.presentation.modulelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.ModuleProgress
import com.shanu.quizflow.feature.quiz.domain.model.ModuleStatus
import com.shanu.quizflow.feature.quiz.domain.model.Subject
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import com.shanu.quizflow.feature.quiz.domain.usecase.GetSubjectsUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.ObserveModuleProgressUseCase
import com.shanu.quizflow.feature.quiz.domain.usecase.SyncSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ModuleListViewModel @Inject constructor(
    private val getSubjects: GetSubjectsUseCase,
    private val observeProgress: ObserveModuleProgressUseCase,
    private val syncSubjects: SyncSubjectsUseCase,
    private val progressRepository: ModuleProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModuleListUiState())
    val uiState: StateFlow<ModuleListUiState> = _uiState.asStateFlow()

    init {
        combine(
            getSubjects(),
            observeProgress(),
            progressRepository.observeActiveSessionSubjectIds(),
        ) { subjects, progressList, activeSubjectIds ->
            val progressMap = progressList.associateBy { it.subjectId }
            subjects.map { subject ->
                val progress = progressMap[subject.id]
                val hasActiveSession = subject.id in activeSubjectIds
                subject.toModuleUi(progress, hasActiveSession)
            }
        }
            .onEach { modules ->
                val activeSession = modules.firstOrNull { it.status == ModuleStatus.IN_PROGRESS }
                val completedModules = modules.filter { it.isCompleted || it.status == ModuleStatus.COMPLETED }
                val completedCount = completedModules.size
                val totalCount = modules.size
                val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
                val totalScore = completedModules.sumOf { it.highScore ?: it.lastScore ?: 0 }
                val avgScore = if (completedCount > 0) totalScore.toFloat() / completedCount else 0f

                _uiState.value = _uiState.value.copy(
                    modules = modules,
                    activeSessionModule = activeSession,
                    overallProgress = OverallProgress(
                        completedModulesCount = completedCount,
                        totalModulesCount = totalCount,
                        completionPercentage = percentage,
                        totalScore = totalScore,
                        averageScore = avgScore,
                    ),
                    syncError = if (modules.isNotEmpty()) null else _uiState.value.syncError,
                    isLoading = false,
                )
            }
            .launchIn(viewModelScope)

        sync()
    }

    fun sync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, syncError = null)
            val isSuccess = syncSubjects() is DataResult.Success
            val cachedCount = withTimeoutOrNull(300.milliseconds) {
                getSubjects().firstOrNull()?.size
            } ?: 0
            val hasCachedModules = _uiState.value.modules.isNotEmpty() || cachedCount > 0
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                isLoading = false,
                syncError = if (!isSuccess && !hasCachedModules)
                    "Failed to load modules. Check your connection."
                else null,
            )
        }
    }

    private fun Subject.toModuleUi(progress: ModuleProgress?, hasActiveSession: Boolean): ModuleUi {
        val isCompleted = (progress?.highScore ?: 0) > 0 || (progress?.isCompleted == true && (progress.lastScore ?: 0) > 0)
        return ModuleUi(
            id = id,
            title = title,
            description = description,
            status = when {
                hasActiveSession -> ModuleStatus.IN_PROGRESS
                isCompleted -> ModuleStatus.COMPLETED
                else -> ModuleStatus.NOT_STARTED
            },
            isCompleted = isCompleted,
            totalQuestions = if ((progress?.totalQuestions ?: 0) > 0) progress?.totalQuestions ?: 10 else 10,
            lastScore = if (isCompleted) progress?.lastScore else null,
            highScore = if (isCompleted) progress?.highScore else null,
        )
    }
}
