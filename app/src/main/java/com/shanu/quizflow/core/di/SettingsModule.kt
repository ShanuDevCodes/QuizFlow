package com.shanu.quizflow.core.di

import com.shanu.quizflow.core.settings.data.ThemePreferenceRepositoryImpl
import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindThemePreferenceRepository(
        impl: ThemePreferenceRepositoryImpl,
    ): ThemePreferenceRepository
}
