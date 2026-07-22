package com.shanu.quizflow.feature.quiz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_session_state",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class QuizSessionStateEntity(
    @PrimaryKey
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    @ColumnInfo(name = "current_index")
    val currentIndex: Int = 0,
    @ColumnInfo(name = "correct_count")
    val correctCount: Int = 0,
    @ColumnInfo(name = "skipped_count")
    val skippedCount: Int = 0,
    @ColumnInfo(name = "current_streak")
    val currentStreak: Int = 0,
    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int = 0,
)
