package com.shanu.quizflow.core.settings.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.FakeThemePreferenceRepository
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetThemeModeUseCaseTest {

    @Test
    fun `invoke delegates to the repository and persists the new mode`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.SYSTEM)
        val useCase = SetThemeModeUseCase(repository)

        useCase(ThemeMode.DARK)

        assertThat(repository.themeMode.value).isEqualTo(ThemeMode.DARK)
        assertThat(repository.setThemeModeCallCount).isEqualTo(1)
    }
}
