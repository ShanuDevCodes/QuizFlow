package com.shanu.quizflow.feature.quiz.data.remote

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class QuizApiTest {

    private val server = MockWebServer()
    private lateinit var api: QuizApi

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Before
    fun setUp() {
        server.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        api = retrofit.create(QuizApi::class.java)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `parses a valid questions array into a list of QuestionDto`() = runTest {
        server.enqueue(
            MockResponse.Builder()
                .body(
                    """
                    [
                        {"id": 1, "question": "What is 2+2?", "options": ["3", "4", "5", "6"], "correctOptionIndex": 1},
                        {"id": 2, "question": "Capital of France?", "options": ["Berlin", "Paris", "Madrid", "Rome"], "correctOptionIndex": 1}
                    ]
                    """.trimIndent(),
                )
                .build(),
        )

        val questions = api.getQuestions()

        assertThat(questions).hasSize(2)
        assertThat(questions[0].id).isEqualTo(1)
        assertThat(questions[0].question).isEqualTo("What is 2+2?")
        assertThat(questions[0].options).containsExactly("3", "4", "5", "6")
        assertThat(questions[1].correctOptionIndex).isEqualTo(1)
    }

    @Test
    fun `preserves unicode characters in question text`() = runTest {
        server.enqueue(
            MockResponse.Builder()
                .body(
                    """[{"id": 1, "question": "Café – what’s new?", "options": ["A", "B", "C", "D"], "correctOptionIndex": 0}]""",
                )
                .build(),
        )

        val questions = api.getQuestions()

        assertThat(questions[0].question).isEqualTo("Café – what’s new?")
    }

    @Test
    fun `an HTTP 500 response surfaces as an HttpException`() = runTest {
        server.enqueue(MockResponse.Builder().code(500).build())

        try {
            api.getQuestions()
            error("Expected HttpException")
        } catch (expected: HttpException) {
        }
    }
}
