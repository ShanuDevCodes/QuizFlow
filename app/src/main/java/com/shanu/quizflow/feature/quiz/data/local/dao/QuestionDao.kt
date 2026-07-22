package com.shanu.quizflow.feature.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shanu.quizflow.feature.quiz.data.local.entity.QuestionEntity

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE subject_id = :subjectId ORDER BY id ASC")
    suspend fun getBySubjectId(subjectId: String): List<QuestionEntity>

    @Upsert
    suspend fun upsertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions WHERE subject_id = :subjectId")
    suspend fun countBySubject(subjectId: String): Int

    @Query("DELETE FROM questions WHERE subject_id = :subjectId")
    suspend fun deleteBySubjectId(subjectId: String)
}
