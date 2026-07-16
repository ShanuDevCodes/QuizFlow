package com.shanu.quizflow.feature.quiz.data.local

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Exercises the real bundled `assets/questions.json` through Robolectric so the offline-fallback
 * source is verified against the actual shipped fixture, not a hand-built stub.
 */
@RunWith(RobolectricTestRunner::class)
class QuizAssetDataSourceImplTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val dataSource = QuizAssetDataSourceImpl(
        context = ApplicationProvider.getApplicationContext(),
        json = json,
    )

    @Test
    fun `reads the bundled questions asset`() = runTest {
        val questions = dataSource.getQuestions()

        assertThat(questions).isNotEmpty()
    }

    @Test
    fun `bundled questions are well-formed - four options and an in-range correct index`() = runTest {
        val questions = dataSource.getQuestions()

        questions.forEach { dto ->
            assertThat(dto.options).hasSize(4)
            assertThat(dto.correctOptionIndex).isIn(0..3)
            assertThat(dto.question).isNotEmpty()
        }
    }
}
