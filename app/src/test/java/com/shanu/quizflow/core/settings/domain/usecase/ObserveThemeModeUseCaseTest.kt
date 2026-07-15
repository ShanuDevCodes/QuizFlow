package com.shanu.quizflow.core.settings.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.FakeThemePreferenceRepository
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveThemeModeUseCaseTest {

    @Test
    fun `invoke emits the repository's current theme mode`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.DARK)
        val useCase = ObserveThemeModeUseCase(repository)

        useCase().test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.DARK)
        }
    }

    @Test
    fun `invoke emits subsequent updates from the repository`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.SYSTEM)
        val useCase = ObserveThemeModeUseCase(repository)

        useCase().test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.SYSTEM)
            repository.setThemeMode(ThemeMode.LIGHT)
            assertThat(awaitItem()).isEqualTo(ThemeMode.LIGHT)
        }
    }
}
