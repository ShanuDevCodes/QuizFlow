package com.shanu.quizflow.core.settings.domain.usecase

import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import javax.inject.Inject

/** Persists a new [ThemeMode] preference. */
class SetThemeModeUseCase @Inject constructor(
    private val repository: ThemePreferenceRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}
