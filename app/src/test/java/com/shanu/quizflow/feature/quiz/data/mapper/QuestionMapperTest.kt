package com.shanu.quizflow.feature.quiz.data.mapper

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.domain.model.Question
import org.junit.Test

class QuestionMapperTest {

    private val mapper = QuestionMapper()

    private fun dto(
        id: Int = 1,
        question: String = "What is the capital of France?",
        options: List<String> = listOf("Berlin", "Paris", "Madrid", "Rome"),
        correctOptionIndex: Int = 1,
    ) = QuestionDto(id, question, options, correctOptionIndex)

    @Test
    fun `map converts a valid dto to a domain Question`() {
        val result = mapper.map(dto())

        assertThat(result).isEqualTo(
            Question(
                id = 1,
                text = "What is the capital of France?",
                options = listOf("Berlin", "Paris", "Madrid", "Rome"),
                correctIndex = 1,
            ),
        )
    }

    @Test
    fun `map preserves unicode characters in question text and options`() {
        val result = mapper.map(
            dto(
                question = "What hidden feature… reveal – tap?",
                options = listOf("Flappy Bird–style game", "Virtual pet", "Hidden menu", "System UI tuner"),
            ),
        )

        assertThat(result.text).isEqualTo("What hidden feature… reveal – tap?")
        assertThat(result.options[0]).isEqualTo("Flappy Bird–style game")
    }

    @Test
    fun `map throws InvalidOptionCount when there are fewer than 4 options`() {
        try {
            mapper.map(dto(options = listOf("A", "B", "C")))
            error("Expected MappingError.InvalidOptionCount")
        } catch (e: MappingError.InvalidOptionCount) {
            assertThat(e.questionId).isEqualTo(1)
            assertThat(e.count).isEqualTo(3)
        }
    }

    @Test
    fun `map throws InvalidOptionCount when there are more than 4 options`() {
        try {
            mapper.map(dto(options = listOf("A", "B", "C", "D", "E")))
            error("Expected MappingError.InvalidOptionCount")
        } catch (e: MappingError.InvalidOptionCount) {
            assertThat(e.count).isEqualTo(5)
        }
    }

    @Test
    fun `map throws InvalidCorrectIndex when the index is negative`() {
        try {
            mapper.map(dto(correctOptionIndex = -1))
            error("Expected MappingError.InvalidCorrectIndex")
        } catch (e: MappingError.InvalidCorrectIndex) {
            assertThat(e.index).isEqualTo(-1)
        }
    }

    @Test
    fun `map throws InvalidCorrectIndex when the index is out of bounds`() {
        try {
            mapper.map(dto(correctOptionIndex = 4))
            error("Expected MappingError.InvalidCorrectIndex")
        } catch (e: MappingError.InvalidCorrectIndex) {
            assertThat(e.index).isEqualTo(4)
        }
    }

    @Test
    fun `map accepts correctOptionIndex at each valid boundary`() {
        assertThat(mapper.map(dto(correctOptionIndex = 0)).correctIndex).isEqualTo(0)
        assertThat(mapper.map(dto(correctOptionIndex = 3)).correctIndex).isEqualTo(3)
    }

    @Test
    fun `mapList maps every dto in order`() {
        val dtos = listOf(dto(id = 1), dto(id = 2), dto(id = 3))
        val result = mapper.mapList(dtos)
        assertThat(result.map { it.id }).containsExactly(1, 2, 3).inOrder()
    }

    @Test
    fun `mapList on an empty list returns an empty list`() {
        assertThat(mapper.mapList(emptyList())).isEmpty()
    }

    @Test
    fun `mapList propagates the first mapping error encountered`() {
        val dtos = listOf(dto(id = 1), dto(id = 2, options = listOf("A", "B")))
        try {
            mapper.mapList(dtos)
            error("Expected MappingError.InvalidOptionCount")
        } catch (e: MappingError.InvalidOptionCount) {
            assertThat(e.questionId).isEqualTo(2)
        }
    }
}
