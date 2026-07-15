package com.shanu.quizflow.core.settings.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.FakeThemePreferenceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveDynamicColorEnabledUseCaseTest {

    @Test
    fun `invoke emits the repository's current dynamic color preference`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = true)
        val useCase = ObserveDynamicColorEnabledUseCase(repository)

        useCase().test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `invoke emits subsequent updates from the repository`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = false)
        val useCase = ObserveDynamicColorEnabledUseCase(repository)

        useCase().test {
            assertThat(awaitItem()).isFalse()
            repository.setDynamicColorEnabled(true)
            assertThat(awaitItem()).isTrue()
        }
    }
}
