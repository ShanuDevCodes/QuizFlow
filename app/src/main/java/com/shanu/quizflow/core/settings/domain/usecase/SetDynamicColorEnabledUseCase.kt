package com.shanu.quizflow.core.settings.domain.usecase

import com.shanu.quizflow.core.settings.domain.repository.ThemePreferenceRepository
import javax.inject.Inject

class SetDynamicColorEnabledUseCase @Inject constructor(
    private val repository: ThemePreferenceRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDynamicColorEnabled(enabled)
}
