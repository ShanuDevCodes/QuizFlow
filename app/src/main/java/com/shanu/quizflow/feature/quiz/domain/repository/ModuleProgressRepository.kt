package com.shanu.quizflow.feature.quiz.domain.repository

import com.shanu.quizflow.feature.quiz.domain.model.ModuleProgress
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import kotlinx.coroutines.flow.Flow

interface ModuleProgressRepository {
    fun observeAllProgress(): Flow<List<ModuleProgress>>
    fun observeActiveSessionSubjectIds(): Flow<Set<String>>
    suspend fun getProgress(subjectId: String): ModuleProgress?
    suspend fun saveResult(subjectId: String, score: Int, total: Int, longestStreak: Int = 0)
    suspend fun saveSessionState(subjectId: String, session: QuizSession)
    suspend fun getSessionState(subjectId: String): QuizSession?
    suspend fun clearSessionState(subjectId: String)
}
