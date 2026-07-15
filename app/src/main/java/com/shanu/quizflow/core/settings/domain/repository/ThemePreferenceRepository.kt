package com.shanu.quizflow.core.settings.domain.repository

import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemePreferenceRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)

    val dynamicColorEnabled: Flow<Boolean>
    suspend fun setDynamicColorEnabled(enabled: Boolean)
}
