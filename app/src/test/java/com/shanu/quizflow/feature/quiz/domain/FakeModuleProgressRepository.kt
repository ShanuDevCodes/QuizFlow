package com.shanu.quizflow.feature.quiz.domain

import com.shanu.quizflow.feature.quiz.domain.model.ModuleProgress
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeModuleProgressRepository : ModuleProgressRepository {

    private val progressFlow = MutableStateFlow<List<ModuleProgress>>(emptyList())
    private val sessionStates = MutableStateFlow<Map<String, QuizSession>>(emptyMap())
    private val savedResults = mutableMapOf<String, Pair<Int, Int>>()

    override fun observeAllProgress(): Flow<List<ModuleProgress>> = progressFlow

    override fun observeActiveSessionSubjectIds(): Flow<Set<String>> =
        sessionStates.map { it.keys }

    override suspend fun getProgress(subjectId: String): ModuleProgress? =
        progressFlow.value.find { it.subjectId == subjectId }

    override suspend fun saveResult(subjectId: String, score: Int, total: Int, longestStreak: Int) {
        savedResults[subjectId] = Pair(score, total)
        val list = progressFlow.value.toMutableList()
        list.removeAll { it.subjectId == subjectId }
        list.add(
            ModuleProgress(
                subjectId = subjectId,
                isCompleted = true,
                lastScore = score,
                highScore = score,
                longestStreak = longestStreak,
                totalQuestions = total,
            )
        )
        progressFlow.value = list
        clearSessionState(subjectId)
    }

    override suspend fun saveSessionState(subjectId: String, session: QuizSession) {
        val current = sessionStates.value.toMutableMap()
        current[subjectId] = session
        sessionStates.value = current
    }

    override suspend fun getSessionState(subjectId: String): QuizSession? = sessionStates.value[subjectId]

    override suspend fun clearSessionState(subjectId: String) {
        val current = sessionStates.value.toMutableMap()
        current.remove(subjectId)
        sessionStates.value = current
    }
}
