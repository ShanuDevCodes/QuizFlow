package com.shanu.quizflow.core.settings.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.settings.domain.model.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ThemePreferenceRepositoryImplTest {

    private lateinit var testFile: File
    private lateinit var repository: ThemePreferenceRepositoryImpl

    @Before
    fun setUp() {
        testFile = File.createTempFile("theme_preferences_test", ".preferences_pb")
        repository = ThemePreferenceRepositoryImpl(newDataStore())
    }

    private fun newDataStore() = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(UnconfinedTestDispatcher()),
        produceFile = { testFile },
    )

    @After
    fun tearDown() {
        testFile.delete()
    }

    @Test
    fun `themeMode defaults to SYSTEM when nothing is persisted`() = runTest {
        repository.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.SYSTEM)
        }
    }

    @Test
    fun `setThemeMode persists the value and themeMode reflects it`() = runTest {
        repository.setThemeMode(ThemeMode.DARK)

        repository.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.DARK)
        }
    }

    @Test
    fun `themeMode falls back to SYSTEM for a corrupt stored value`() = runTest {
        val dataStore = newDataStore()
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("theme_mode")] = "NOT_A_REAL_MODE"
        }
        val corruptedRepository = ThemePreferenceRepositoryImpl(dataStore)

        corruptedRepository.themeMode.test {
            assertThat(awaitItem()).isEqualTo(ThemeMode.SYSTEM)
        }
    }
}
