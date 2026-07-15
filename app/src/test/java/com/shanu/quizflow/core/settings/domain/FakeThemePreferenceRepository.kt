package com.shanu.quizflow.core.settings.domain

import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** In-memory fake used across settings unit tests instead of mocking. */
class FakeThemePreferenceRepository(
    initial: ThemeMode = ThemeMode.SYSTEM,
) : ThemePreferenceRepository {

    private val state = MutableStateFlow(initial)
    override val themeMode: StateFlow<ThemeMode> = state

    var setThemeModeCallCount: Int = 0
        private set

    override suspend fun setThemeMode(mode: ThemeMode) {
        setThemeModeCallCount++
        state.value = mode
    }
}
