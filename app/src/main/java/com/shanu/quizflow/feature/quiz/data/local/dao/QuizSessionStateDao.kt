package com.shanu.quizflow.feature.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shanu.quizflow.feature.quiz.data.local.entity.QuizSessionStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizSessionStateDao {

    @Query("SELECT * FROM quiz_session_state WHERE subject_id = :subjectId")
    suspend fun getSessionState(subjectId: String): QuizSessionStateEntity?

    @Query("SELECT * FROM quiz_session_state")
    fun observeAllSessionStates(): Flow<List<QuizSessionStateEntity>>

    @Upsert
    suspend fun upsertSessionState(state: QuizSessionStateEntity)

    @Query("DELETE FROM quiz_session_state WHERE subject_id = :subjectId")
    suspend fun deleteSessionState(subjectId: String)
}
