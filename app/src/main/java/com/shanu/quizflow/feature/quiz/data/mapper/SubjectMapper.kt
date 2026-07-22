package com.shanu.quizflow.feature.quiz.data.mapper

import com.shanu.quizflow.feature.quiz.data.local.entity.SubjectEntity
import com.shanu.quizflow.feature.quiz.data.remote.dto.SubjectDto
import com.shanu.quizflow.feature.quiz.domain.model.Subject
import javax.inject.Inject

class SubjectMapper @Inject constructor() {

    fun dtoToEntity(dto: SubjectDto, order: Int): SubjectEntity = SubjectEntity(
        id = dto.id,
        title = dto.title,
        description = dto.description,
        questionsUrl = dto.questionsUrl,
        displayOrder = order,
    )

    fun dtoListToEntities(dtos: List<SubjectDto>): List<SubjectEntity> =
        dtos.mapIndexed { index, dto -> dtoToEntity(dto, index) }

    fun entityToDomain(entity: SubjectEntity): Subject = Subject(
        id = entity.id,
        title = entity.title,
        description = entity.description,
        questionsUrl = entity.questionsUrl,
    )

    fun entityListToDomain(entities: List<SubjectEntity>): List<Subject> =
        entities.map(::entityToDomain)
}
