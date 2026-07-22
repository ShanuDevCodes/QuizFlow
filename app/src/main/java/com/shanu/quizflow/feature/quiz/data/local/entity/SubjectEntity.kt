package com.shanu.quizflow.feature.quiz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "questions_url")
    val questionsUrl: String,
    @ColumnInfo(name = "display_order")
    val displayOrder: Int,
)
