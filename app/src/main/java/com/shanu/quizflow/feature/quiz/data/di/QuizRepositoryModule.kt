package com.shanu.quizflow.feature.quiz.data.di

import com.shanu.quizflow.feature.quiz.data.remote.QuizRemoteDataSource
import com.shanu.quizflow.feature.quiz.data.remote.QuizRemoteDataSourceImpl
import com.shanu.quizflow.feature.quiz.data.repository.ModuleProgressRepositoryImpl
import com.shanu.quizflow.feature.quiz.data.repository.QuizRepositoryImpl
import com.shanu.quizflow.feature.quiz.domain.repository.ModuleProgressRepository
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuizRemoteDataSource(impl: QuizRemoteDataSourceImpl): QuizRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindModuleProgressRepository(impl: ModuleProgressRepositoryImpl): ModuleProgressRepository
}
