package com.shanu.quizflow.feature.quiz.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object QuizPresentationModule {

    @Provides
    @RevealDurationMillis
    fun provideRevealDurationMillis(): Long = 1_000L
}
