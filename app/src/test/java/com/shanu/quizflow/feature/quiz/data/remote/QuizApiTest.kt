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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@RunWith(RobolectricTestRunner::class)
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
    fun `parses subjects array into a list of SubjectDto`() = runTest {
        server.enqueue(
            MockResponse.Builder()
                .body(
                    """
                    [
                        {"id": "android_basics", "title": "Android Basics", "description": "Fundamentals", "questions_url": "https://example.com/q.json"}
                    ]
                    """.trimIndent(),
                )
                .build(),
        )

        val subjects = api.getSubjects()

        assertThat(subjects).hasSize(1)
        assertThat(subjects[0].id).isEqualTo("android_basics")
        assertThat(subjects[0].title).isEqualTo("Android Basics")
    }

    @Test
    fun `parses a valid questions array into a list of QuestionDto via getQuestionsByUrl`() = runTest {
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

        val questions = api.getQuestionsByUrl(server.url("/questions.json").toString())

        assertThat(questions).hasSize(2)
        assertThat(questions[0].id).isEqualTo(1)
        assertThat(questions[0].question).isEqualTo("What is 2+2?")
        assertThat(questions[0].stringOptions).containsExactly("3", "4", "5", "6")
        assertThat(questions[1].correctOptionIndex).isEqualTo(1)
    }

    @Test
    fun `an HTTP 500 response surfaces as an HttpException`() = runTest {
        server.enqueue(MockResponse.Builder().code(500).build())

        try {
            api.getSubjects()
            error("Expected HttpException")
        } catch (expected: HttpException) {
        }
    }
}
