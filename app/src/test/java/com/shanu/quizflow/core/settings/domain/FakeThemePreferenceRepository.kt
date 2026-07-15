package com.shanu.quizflow.core.settings.domain

import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeThemePreferenceRepository(
    initial: ThemeMode = ThemeMode.SYSTEM,
    initialDynamicColorEnabled: Boolean = false,
) : ThemePreferenceRepository {

    private val state = MutableStateFlow(initial)
    override val themeMode: StateFlow<ThemeMode> = state

    var setThemeModeCallCount: Int = 0
        private set

    override suspend fun setThemeMode(mode: ThemeMode) {
        setThemeModeCallCount++
        state.value = mode
    }

    private val dynamicColorState = MutableStateFlow(initialDynamicColorEnabled)
    override val dynamicColorEnabled: StateFlow<Boolean> = dynamicColorState

    var setDynamicColorEnabledCallCount: Int = 0
        private set

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        setDynamicColorEnabledCallCount++
        dynamicColorState.value = enabled
    }
}
