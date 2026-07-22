package com.shanu.quizflow.feature.quiz.data.repository

import com.shanu.quizflow.core.coroutines.DispatcherProvider
import com.shanu.quizflow.feature.quiz.data.local.dao.ModuleProgressDao
import com.shanu.quizflow.feature.quiz.data.local.dao.QuestionDao
import com.shanu.quizflow.feature.quiz.data.local.dao.QuizSessionStateDao
import com.shanu.quizflow.feature.quiz.data.local.entity.ModuleProgressEntity
import com.shanu.quizflow.feature.quiz.data.local.entity.QuizSessionStateEntity
import com.shanu.quizflow.feature.quiz.data.mapper.QuestionMapper
import com.shanu.quizflow.feature.quiz.domain.model.ModuleProgress
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ModuleProgressRepositoryImpl @Inject constructor(
    private val progressDao: ModuleProgressDao,
    private val sessionStateDao: QuizSessionStateDao,
    private val questionDao: QuestionDao,
    private val questionMapper: QuestionMapper,
    private val dispatcherProvider: DispatcherProvider,
) : ModuleProgressRepository {

    override fun observeAllProgress(): Flow<List<ModuleProgress>> =
        progressDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeActiveSessionSubjectIds(): Flow<Set<String>> =
        sessionStateDao.observeAllSessionStates().map { list -> list.map { it.subjectId }.toSet() }

    override suspend fun getProgress(subjectId: String): ModuleProgress? =
        withContext(dispatcherProvider.io) {
            progressDao.getBySubjectId(subjectId)?.toDomain()
        }

    override suspend fun saveResult(subjectId: String, score: Int, total: Int, longestStreak: Int) =
        withContext(dispatcherProvider.io) {
            val existing = progressDao.getBySubjectId(subjectId)
            val highScore = maxOf(existing?.highScore ?: 0, score)
            val bestStreak = maxOf(existing?.longestStreak ?: 0, longestStreak)
            val isCompleted = highScore > 0 || (existing?.isCompleted == true)
            progressDao.upsert(
                ModuleProgressEntity(
                    subjectId = subjectId,
                    isCompleted = isCompleted,
                    lastScore = score,
                    highScore = highScore,
                    longestStreak = bestStreak,
                    totalQuestions = total,
                    lastAttemptedAt = System.currentTimeMillis(),
                )
            )
            sessionStateDao.deleteSessionState(subjectId)
        }

    override suspend fun saveSessionState(subjectId: String, session: QuizSession) =
        withContext(dispatcherProvider.io) {
            sessionStateDao.upsertSessionState(
                QuizSessionStateEntity(
                    subjectId = subjectId,
                    currentIndex = session.currentIndex,
                    correctCount = session.correctCount,
                    skippedCount = session.skippedCount,
                    currentStreak = session.currentStreak,
                    longestStreak = session.longestStreak,
                )
            )
        }

    override suspend fun getSessionState(subjectId: String): QuizSession? =
        withContext(dispatcherProvider.io) {
            val state = sessionStateDao.getSessionState(subjectId) ?: return@withContext null
            val questions = questionDao.getBySubjectId(subjectId)
            if (questions.isEmpty()) return@withContext null
            QuizSession(
                questions = questionMapper.entityListToDomain(questions),
                currentIndex = state.currentIndex,
                correctCount = state.correctCount,
                skippedCount = state.skippedCount,
                currentStreak = state.currentStreak,
                longestStreak = state.longestStreak,
            )
        }

    override suspend fun clearSessionState(subjectId: String) =
        withContext(dispatcherProvider.io) {
            sessionStateDao.deleteSessionState(subjectId)
        }

    private fun ModuleProgressEntity.toDomain() = ModuleProgress(
        subjectId = subjectId,
        isCompleted = isCompleted,
        lastScore = lastScore,
        highScore = highScore,
        longestStreak = longestStreak,
        totalQuestions = totalQuestions,
    )
}
