package com.shanu.quizflow.core.settings.domain.repository

import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/** Persists and observes the user's [ThemeMode] preference. Implemented in the data layer. */
interface ThemePreferenceRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
