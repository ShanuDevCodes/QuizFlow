package com.shanu.quizflow.feature.quiz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "module_progress",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ModuleProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "last_score")
    val lastScore: Int = 0,
    @ColumnInfo(name = "high_score")
    val highScore: Int = 0,
    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,
    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int = 0,
    @ColumnInfo(name = "last_attempted_at")
    val lastAttemptedAt: Long = 0L,
)
