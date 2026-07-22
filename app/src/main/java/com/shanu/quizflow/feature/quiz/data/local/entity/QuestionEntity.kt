package com.shanu.quizflow.feature.quiz.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "questions",
    primaryKeys = ["id", "subject_id"],
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class QuestionEntity(
    val id: Int,
    @ColumnInfo(name = "subject_id", index = true)
    val subjectId: String,
    @ColumnInfo(name = "question_text")
    val questionText: String,
    @ColumnInfo(name = "option_0")
    val option0: String,
    @ColumnInfo(name = "option_1")
    val option1: String,
    @ColumnInfo(name = "option_2")
    val option2: String,
    @ColumnInfo(name = "option_3")
    val option3: String,
    @ColumnInfo(name = "correct_option_index")
    val correctOptionIndex: Int,
)
