package com.shanu.quizflow.feature.quiz.data.remote.dto

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Test

class QuestionDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun sanitizeJson(raw: String): String =
        raw.replace(Regex(",\\s*([\\]}])"), "$1")

    @Test
    fun `decodes a single valid question object`() {
        val raw = """
            {"id":1,"question":"What is the capital of France?","options":["Berlin","Paris","Madrid","Rome"],"correctOptionIndex":1}
        """.trimIndent()

        val dto = json.decodeFromString<QuestionDto>(raw)

        assertThat(dto.id).isEqualTo(1)
        assertThat(dto.question).isEqualTo("What is the capital of France?")
        assertThat(dto.stringOptions).containsExactly("Berlin", "Paris", "Madrid", "Rome").inOrder()
        assertThat(dto.correctOptionIndex).isEqualTo(1)
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
        assertThat(dto.stringOptions[0]).isEqualTo("Flappy Bird–style game")
    }

    @Test
    fun `decodes correctly when correctOption alternative field name is used`() {
        val raw = """
            {"id":7,"question":"What is used in Compose to recompute UI when state changes?","options":["StateFlow","LiveData","MutableState","setState()"],"correctOption":2}
        """.trimIndent()

        val dto = json.decodeFromString<QuestionDto>(raw)

        assertThat(dto.id).isEqualTo(7)
        assertThat(dto.correctOptionIndex).isEqualTo(2)
    }

    @Test
    fun `decodes correctly when an option is a raw integer`() {
        val raw = """
            {"id":9,"question":"What is the benefit of using biometric authentication?","options":["Fast","Offline","Secure",10],"correctOptionIndex":2}
        """.trimIndent()

        val dto = json.decodeFromString<QuestionDto>(raw)

        assertThat(dto.stringOptions[3]).isEqualTo("10")
    }

    @Test
    fun `decodes correctly when options array contains trailing commas`() {
        val raw = """
            [
              {
                "id": 9,
                "question": "What is the benefit of using biometric authentication APIs in Android?",
                "options": [
                  "Faster screen transitions",
                  "Offline functionality",
                  "Secure and user-friendly authentication",
                  10,
                ],
                "correctOptionIndex": 2
              }
            ]
        """.trimIndent()

        val sanitized = sanitizeJson(raw)
        val dtos = json.decodeFromString<List<QuestionDto>>(sanitized)

        assertThat(dtos).hasSize(1)
        assertThat(dtos[0].stringOptions[3]).isEqualTo("10")
        assertThat(dtos[0].correctOptionIndex).isEqualTo(2)
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
}
