package com.shanu.quizflow.feature.quiz.data.mapper

import com.shanu.quizflow.feature.quiz.data.local.entity.QuestionEntity
import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.domain.model.Question
import javax.inject.Inject

class QuestionMapper @Inject constructor() {

    fun map(dto: QuestionDto): Question {
        val options = dto.stringOptions
        if (options.size != 4) {
            throw MappingError.InvalidOptionCount(dto.id, options.size)
        }
        if (dto.correctOptionIndex !in options.indices) {
            throw MappingError.InvalidCorrectIndex(dto.id, dto.correctOptionIndex)
        }
        return Question(
            id = dto.id,
            text = dto.question,
            options = options,
            correctIndex = dto.correctOptionIndex,
        )
    }

    fun mapList(dtos: List<QuestionDto>): List<Question> = dtos.map(::map)

    fun dtoToEntity(dto: QuestionDto, subjectId: String): QuestionEntity {
        val options = dto.stringOptions
        return QuestionEntity(
            id = dto.id,
            subjectId = subjectId,
            questionText = dto.question,
            option0 = options.getOrElse(0) { "" },
            option1 = options.getOrElse(1) { "" },
            option2 = options.getOrElse(2) { "" },
            option3 = options.getOrElse(3) { "" },
            correctOptionIndex = dto.correctOptionIndex,
        )
    }

    fun dtoListToEntities(dtos: List<QuestionDto>, subjectId: String): List<QuestionEntity> =
        dtos.map { dtoToEntity(it, subjectId) }

    fun entityToDomain(entity: QuestionEntity): Question = Question(
        id = entity.id,
        text = entity.questionText,
        options = listOf(entity.option0, entity.option1, entity.option2, entity.option3),
        correctIndex = entity.correctOptionIndex,
    )

    fun entityListToDomain(entities: List<QuestionEntity>): List<Question> =
        entities.map(::entityToDomain)
}