package com.shanu.quizflow.core.settings.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.FakeThemePreferenceRepository
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import com.shanu.quizflow.core.settings.domain.usecase.ObserveDynamicColorEnabledUseCase
import com.shanu.quizflow.core.settings.domain.usecase.ObserveThemeModeUseCase
import com.shanu.quizflow.core.settings.domain.usecase.SetDynamicColorEnabledUseCase
import com.shanu.quizflow.core.settings.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(repository: FakeThemePreferenceRepository): ThemeViewModel =
        ThemeViewModel(
            observeThemeMode = ObserveThemeModeUseCase(repository),
            setThemeMode = SetThemeModeUseCase(repository),
            observeDynamicColorEnabled = ObserveDynamicColorEnabledUseCase(repository),
            setDynamicColorEnabled = SetDynamicColorEnabledUseCase(repository),
        )

    @Test
    fun `themeMode starts with the persisted value`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.DARK)
        val sut = viewModel(repository)

        sut.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.DARK)
        }
    }

    @Test
    fun `onToggleTheme cycles Light to Dark`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.LIGHT)
        val sut = viewModel(repository)

        sut.onToggleTheme()

        sut.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.DARK)
        }
    }

    @Test
    fun `onToggleTheme cycles Dark to System`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.DARK)
        val sut = viewModel(repository)

        sut.onToggleTheme()

        sut.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.SYSTEM)
        }
    }

    @Test
    fun `onToggleTheme cycles System to Light`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.SYSTEM)
        val sut = viewModel(repository)

        sut.onToggleTheme()

        sut.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.LIGHT)
        }
    }

    @Test
    fun `onToggleTheme persists the new mode through the repository`() = runTest {
        val repository = FakeThemePreferenceRepository(initial = ThemeMode.LIGHT)
        val sut = viewModel(repository)

        sut.onToggleTheme()

        assertThat(repository.setThemeModeCallCount).isEqualTo(1)
    }

    @Test
    fun `dynamicColorEnabled starts with the persisted value`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = true)
        val sut = viewModel(repository)

        sut.dynamicColorEnabled.test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `onToggleDynamicColor flips false to true`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = false)
        val sut = viewModel(repository)

        sut.onToggleDynamicColor()

        sut.dynamicColorEnabled.test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `onToggleDynamicColor flips true to false`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = true)
        val sut = viewModel(repository)

        sut.onToggleDynamicColor()

        sut.dynamicColorEnabled.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `onToggleDynamicColor persists the new preference through the repository`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = false)
        val sut = viewModel(repository)

        sut.onToggleDynamicColor()

        assertThat(repository.setDynamicColorEnabledCallCount).isEqualTo(1)
    }
}
