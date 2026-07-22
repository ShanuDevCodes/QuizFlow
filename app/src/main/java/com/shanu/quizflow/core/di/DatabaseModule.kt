package com.shanu.quizflow.core.di

import android.content.Context
import androidx.room.Room
import com.shanu.quizflow.core.database.QuizFlowDatabase
import com.shanu.quizflow.feature.quiz.data.local.dao.ModuleProgressDao
import com.shanu.quizflow.feature.quiz.data.local.dao.QuestionDao
import com.shanu.quizflow.feature.quiz.data.local.dao.QuizSessionStateDao
import com.shanu.quizflow.feature.quiz.data.local.dao.SubjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuizFlowDatabase =
        Room.databaseBuilder(
            context,
            QuizFlowDatabase::class.java,
            "quizflow.db",
        ).build()

    @Provides
    fun provideSubjectDao(db: QuizFlowDatabase): SubjectDao = db.subjectDao()

    @Provides
    fun provideQuestionDao(db: QuizFlowDatabase): QuestionDao = db.questionDao()

    @Provides
    fun provideModuleProgressDao(db: QuizFlowDatabase): ModuleProgressDao = db.moduleProgressDao()

    @Provides
    fun provideQuizSessionStateDao(db: QuizFlowDatabase): QuizSessionStateDao = db.quizSessionStateDao()
}
