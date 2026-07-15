package com.shanu.quizflow.core.settings.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.FakeThemePreferenceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetDynamicColorEnabledUseCaseTest {

    @Test
    fun `invoke delegates to the repository and persists the new preference`() = runTest {
        val repository = FakeThemePreferenceRepository(initialDynamicColorEnabled = false)
        val useCase = SetDynamicColorEnabledUseCase(repository)

        useCase(true)

        assertThat(repository.dynamicColorEnabled.value).isTrue()
        assertThat(repository.setDynamicColorEnabledCallCount).isEqualTo(1)
    }
}
