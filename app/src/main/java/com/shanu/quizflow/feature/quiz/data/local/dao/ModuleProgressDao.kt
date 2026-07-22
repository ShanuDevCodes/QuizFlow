package com.shanu.quizflow.feature.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shanu.quizflow.feature.quiz.data.local.entity.ModuleProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleProgressDao {

    @Query("SELECT * FROM module_progress WHERE subject_id = :subjectId")
    suspend fun getBySubjectId(subjectId: String): ModuleProgressEntity?

    @Query("SELECT * FROM module_progress")
    fun observeAll(): Flow<List<ModuleProgressEntity>>

    @Upsert
    suspend fun upsert(progress: ModuleProgressEntity)
}
