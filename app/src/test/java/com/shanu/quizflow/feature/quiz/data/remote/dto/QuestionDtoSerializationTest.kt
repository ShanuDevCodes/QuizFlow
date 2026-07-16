package com.shanu.quizflow.feature.quiz.data.remote.dto

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File

class QuestionDtoSerializationTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun `decodes a single valid question object`() {
        val raw = """
            {"id":1,"question":"What is the capital of France?","options":["Berlin","Paris","Madrid","Rome"],"correctOptionIndex":1}
        """.trimIndent()

        val dto = json.decodeFromString<QuestionDto>(raw)

        assertThat(dto).isEqualTo(QuestionDto(1, "What is the capital of France?", listOf("Berlin", "Paris", "Madrid", "Rome"), 1))
    }

    @Test
    fun `decodes a list of questions`() {
        val raw = """
            [
              {"id":1,"question":"Q1","options":["A","B","C","D"],"correctOptionIndex":0},
              {"id":2,"question":"Q2","options":["A","B","C","D"],"correctOptionIndex":2}
            ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<QuestionDto>>(raw)

        assertThat(dtos).hasSize(2)
        assertThat(dtos[1].id).isEqualTo(2)
        assertThat(dtos[1].correctOptionIndex).isEqualTo(2)
    }

    @Test
    fun `decodes unicode characters correctly`() {
        val raw = """
            {"id":1,"question":"What hidden feature… tap – reveal?","options":["Flappy Bird–style game","B","C","D"],"correctOptionIndex":0}
        """.trimIndent()

        val dto = json.decodeFromString<QuestionDto>(raw)

        assertThat(dto.question).isEqualTo("What hidden feature… tap – reveal?")
        assertThat(dto.options[0]).isEqualTo("Flappy Bird–style game")
    }

    @Test
    fun `ignores unknown keys instead of failing`() {
        val raw = """
            {"id":1,"question":"Q","options":["A","B","C","D"],"correctOptionIndex":0,"category":"android","difficulty":"easy"}
        """.trimIndent()

        val dto = json.decodeFromString<QuestionDto>(raw)

        assertThat(dto.id).isEqualTo(1)
    }

    @Test
    fun `throws on malformed JSON`() {
        try {
            json.decodeFromString<QuestionDto>("{ not valid json")
            error("Expected a SerializationException")
        } catch (expected: SerializationException) {
        }
    }

    @Test
    fun `throws when a required field is missing`() {
        val raw = """{"id":1,"options":["A","B","C","D"],"correctOptionIndex":0}"""
        try {
            json.decodeFromString<QuestionDto>(raw)
            error("Expected a SerializationException")
        } catch (expected: SerializationException) {
        }
    }

    @Test
    fun `the bundled questions asset decodes into exactly 10 valid questions`() {
        val file = File("src/main/assets/questions.json")
        val dtos = json.decodeFromString<List<QuestionDto>>(file.readText())

        assertThat(dtos).hasSize(10)
        dtos.forEach { dto ->
            assertThat(dto.options).hasSize(4)
            assertThat(dto.correctOptionIndex).isIn(dto.options.indices)
        }
    }
}
