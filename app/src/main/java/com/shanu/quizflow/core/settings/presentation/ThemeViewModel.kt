package com.shanu.quizflow.core.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.usecase.ObserveDynamicColorEnabledUseCase
import com.shanu.quizflow.core.settings.domain.usecase.ObserveThemeModeUseCase
import com.shanu.quizflow.core.settings.domain.usecase.SetDynamicColorEnabledUseCase
import com.shanu.quizflow.core.settings.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    observeDynamicColorEnabled: ObserveDynamicColorEnabledUseCase,
    private val setDynamicColorEnabled: SetDynamicColorEnabledUseCase,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = observeThemeMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val dynamicColorEnabled: StateFlow<Boolean> = observeDynamicColorEnabled()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun onToggleTheme() {
        viewModelScope.launch {
            setThemeMode(themeMode.value.next())
        }
    }

    /** Flips the wallpaper-derived dynamic color preference (Android 12+ only). */
    fun onToggleDynamicColor() {
        viewModelScope.launch {
            setDynamicColorEnabled(!dynamicColorEnabled.value)
        }
    }
}
