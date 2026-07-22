package com.shanu.quizflow.feature.quiz.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class QuestionDto(
    @SerialName("id") val idElement: JsonElement? = null,
    val question: String,
    val options: List<JsonElement> = emptyList(),
    @SerialName("correctOptionIndex") val correctOptionIndexRaw: JsonElement? = null,
    @SerialName("correctOption") val correctOptionAlt: JsonElement? = null,
) {
    constructor(
        id: Int,
        question: String,
        options: List<String>,
        correctOptionIndex: Int,
    ) : this(
        idElement = JsonPrimitive(id),
        question = question,
        options = options.map { JsonPrimitive(it) },
        correctOptionIndexRaw = JsonPrimitive(correctOptionIndex),
    )

    val id: Int
        get() {
            val element = idElement ?: return 1
            return element.jsonPrimitive.intOrNull
                ?: element.jsonPrimitive.content.toIntOrNull()
                ?: 1
        }

    val correctOptionIndex: Int
        get() {
            val element = correctOptionIndexRaw ?: correctOptionAlt ?: return 0
            return element.jsonPrimitive.intOrNull
                ?: element.jsonPrimitive.content.toIntOrNull()
                ?: 0
        }

    val stringOptions: List<String>
        get() = options.map { element ->
            runCatching { element.jsonPrimitive.content }.getOrDefault(element.toString())
        }
}
