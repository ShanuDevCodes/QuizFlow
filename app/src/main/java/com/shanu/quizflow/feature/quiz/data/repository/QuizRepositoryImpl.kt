package com.shanu.quizflow.feature.quiz.data.repository

import com.shanu.quizflow.core.coroutines.DispatcherProvider
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.data.local.QuizAssetDataSource
import com.shanu.quizflow.feature.quiz.data.mapper.MappingError
import com.shanu.quizflow.feature.quiz.data.mapper.QuestionMapper
import com.shanu.quizflow.feature.quiz.data.remote.QuizRemoteDataSource
import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val remoteDataSource: QuizRemoteDataSource,
    private val assetDataSource: QuizAssetDataSource,
    private val mapper: QuestionMapper,
    private val dispatcherProvider: DispatcherProvider,
) : QuizRepository {

    override suspend fun getQuestions(): DataResult<List<Question>> = withContext(dispatcherProvider.io) {
        val dtosResult = runCatching { remoteDataSource.getQuestions() }
            .recoverCatching { assetDataSource.getQuestions() }

        dtosResult.fold(
            onSuccess = { dtos -> mapToResult(dtos) },
            onFailure = { error -> DataResult.Error(error.toAppError()) },
        )
    }

    private fun mapToResult(dtos: List<QuestionDto>): DataResult<List<Question>> = try {
        val questions = mapper.mapList(dtos)
        if (questions.isEmpty()) {
            DataResult.Error(AppError.Mapping("No questions were returned."))
        } else {
            DataResult.Success(questions)
        }
    } catch (e: MappingError) {
        DataResult.Error(e.toAppError())
    }

    private fun Throwable.toAppError(): AppError = when (this) {
        is MappingError -> AppError.Mapping(message.orEmpty())
        is HttpException -> AppError.ServerError
        is IOException -> AppError.Network
        else -> AppError.Unknown(this)
    }
}
