package com.shanu.quizflow.feature.quiz.data.repository

import com.shanu.quizflow.core.coroutines.DispatcherProvider
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.core.result.toAppError
import com.shanu.quizflow.feature.quiz.data.local.dao.QuestionDao
import com.shanu.quizflow.feature.quiz.data.local.dao.SubjectDao
import com.shanu.quizflow.feature.quiz.data.mapper.QuestionMapper
import com.shanu.quizflow.feature.quiz.data.mapper.SubjectMapper
import com.shanu.quizflow.feature.quiz.data.remote.QuizRemoteDataSource
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.model.Subject
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val remoteDataSource: QuizRemoteDataSource,
    private val subjectDao: SubjectDao,
    private val questionDao: QuestionDao,
    private val subjectMapper: SubjectMapper,
    private val questionMapper: QuestionMapper,
    private val dispatcherProvider: DispatcherProvider,
) : QuizRepository {

    override fun observeSubjects(): Flow<List<Subject>> =
        subjectDao.observeAll().map { subjectMapper.entityListToDomain(it) }

    override suspend fun syncSubjects(): DataResult<Unit> = withContext(dispatcherProvider.io) {
        try {
            val dto = remoteDataSource.getSubjects()
            val entities = subjectMapper.dtoListToEntities(dto)
            subjectDao.upsertAll(entities)

            coroutineScope {
                dto.map { dto ->
                    async {
                        try {
                            val questionDtos = remoteDataSource.getQuestionsByUrl(dto.questionsUrl)
                            val questionEntities = questionMapper.dtoListToEntities(questionDtos, dto.id)
                            questionDao.deleteBySubjectId(dto.id)
                            questionDao.upsertAll(questionEntities)
                        } catch (_: Exception) {
                            // Skip individual questions fetch failure
                        }
                    }
                }.awaitAll()
            }
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e.toAppError())
        }
    }

    override suspend fun getQuestions(subjectId: String): DataResult<List<Question>> =
        withContext(dispatcherProvider.io) {
            val subject = subjectDao.getAll().find { it.id == subjectId }
            var networkError: AppError? = null

            if (subject != null) {
                try {
                    val dto = remoteDataSource.getQuestionsByUrl(subject.questionsUrl)
                    val entities = questionMapper.dtoListToEntities(dto, subjectId)
                    questionDao.deleteBySubjectId(subjectId)
                    questionDao.upsertAll(entities)
                    return@withContext DataResult.Success(questionMapper.entityListToDomain(entities))
                } catch (e: Exception) {
                    networkError = e.toAppError()
                }
            }

            val cached = questionDao.getBySubjectId(subjectId)
            if (cached.isNotEmpty()) {
                return@withContext DataResult.Success(questionMapper.entityListToDomain(cached))
            }

            DataResult.Error(networkError ?: AppError.Network)
        }
}
