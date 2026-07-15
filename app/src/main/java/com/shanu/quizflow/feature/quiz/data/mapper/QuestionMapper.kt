package com.shanu.quizflow.feature.quiz.data.mapper

import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.domain.model.Question
import javax.inject.Inject

class QuestionMapper @Inject constructor() {

    fun map(dto: QuestionDto): Question {
        if (dto.options.size != 4) {
            throw MappingError.InvalidOptionCount(dto.id, dto.options.size)
        }
        if (dto.correctOptionIndex !in dto.options.indices) {
            throw MappingError.InvalidCorrectIndex(dto.id, dto.correctOptionIndex)
        }
        return Question(
            id = dto.id,
            text = dto.question,
            options = dto.options,
            correctIndex = dto.correctOptionIndex,
        )
    }

    fun mapList(dtos: List<QuestionDto>): List<Question> = dtos.map(::map)
}
