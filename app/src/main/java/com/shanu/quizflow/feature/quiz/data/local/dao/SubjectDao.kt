package com.shanu.quizflow.feature.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shanu.quizflow.feature.quiz.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects ORDER BY display_order ASC")
    fun observeAll(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects ORDER BY display_order ASC")
    suspend fun getAll(): List<SubjectEntity>

    @Upsert
    suspend fun upsertAll(subjects: List<SubjectEntity>)

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int
}
