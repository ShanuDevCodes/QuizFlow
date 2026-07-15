package com.shanu.quizflow.core.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemePreferenceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : ThemePreferenceRepository {

    override val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        preferences[THEME_MODE_KEY]?.let { stored ->
            runCatching { ThemeMode.valueOf(stored) }.getOrNull()
        } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = mode.name }
    }

    override val dynamicColorEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_ENABLED_KEY] ?: false
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[DYNAMIC_COLOR_ENABLED_KEY] = enabled }
    }

    companion object {
        val THEME_MODE_KEY: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR_ENABLED_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("dynamic_color_enabled")
    }
}
