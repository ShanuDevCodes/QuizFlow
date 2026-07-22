package com.shanu.quizflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shanu.quizflow.feature.quiz.data.local.dao.ModuleProgressDao
import com.shanu.quizflow.feature.quiz.data.local.dao.QuestionDao
import com.shanu.quizflow.feature.quiz.data.local.dao.QuizSessionStateDao
import com.shanu.quizflow.feature.quiz.data.local.dao.SubjectDao
import com.shanu.quizflow.feature.quiz.data.local.entity.ModuleProgressEntity
import com.shanu.quizflow.feature.quiz.data.local.entity.QuestionEntity
import com.shanu.quizflow.feature.quiz.data.local.entity.QuizSessionStateEntity
import com.shanu.quizflow.feature.quiz.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        QuestionEntity::class,
        ModuleProgressEntity::class,
        QuizSessionStateEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class QuizFlowDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun questionDao(): QuestionDao
    abstract fun moduleProgressDao(): ModuleProgressDao
    abstract fun quizSessionStateDao(): QuizSessionStateDao
}
