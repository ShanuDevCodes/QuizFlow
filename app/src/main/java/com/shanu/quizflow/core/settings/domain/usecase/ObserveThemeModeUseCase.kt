package com.shanu.quizflow.core.settings.domain.usecase

import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThemeModeUseCase @Inject constructor(
    private val repository: ThemePreferenceRepository,
) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode
}
