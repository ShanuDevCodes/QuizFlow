package com.shanu.quizflow.core.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.usecase.ObserveThemeModeUseCase
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
) : ViewModel() {

    // Eagerly (not WhileSubscribed): onToggleTheme reads themeMode.value directly, which is
    // only accurate once the upstream preference has actually been collected at least once.
    val themeMode: StateFlow<ThemeMode> = observeThemeMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    /** Cycles Light -> Dark -> System -> Light, per [ThemeMode.next]. */
    fun onToggleTheme() {
        viewModelScope.launch {
            setThemeMode(themeMode.value.next())
        }
    }
}
