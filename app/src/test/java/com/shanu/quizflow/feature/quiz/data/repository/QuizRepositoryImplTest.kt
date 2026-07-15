package com.shanu.quizflow.feature.quiz.data.repository

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.coroutines.FakeDispatcherProvider
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.data.local.FakeQuizAssetDataSource
import com.shanu.quizflow.feature.quiz.data.mapper.QuestionMapper
import com.shanu.quizflow.feature.quiz.data.remote.FakeQuizRemoteDataSource
import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class QuizRepositoryImplTest {

    private val remote = FakeQuizRemoteDataSource()
    private val asset = FakeQuizAssetDataSource()
    private val repository = QuizRepositoryImpl(
        remoteDataSource = remote,
        assetDataSource = asset,
        mapper = QuestionMapper(),
        dispatcherProvider = FakeDispatcherProvider(Dispatchers.Unconfined),
    )

    private fun validDto(id: Int = 1) = QuestionDto(id, "Q$id", listOf("A", "B", "C", "D"), 0)

    private fun httpException(code: Int = 500): HttpException {
        val body = "".toResponseBody(null)
        return HttpException(Response.error<Any>(code, body))
    }

    @Test
    fun `returns Success with mapped questions when the remote source succeeds`() = runTest {
        remote.setResult(listOf(validDto(1), validDto(2)))

        val result = repository.getQuestions()

        assertThat(result).isInstanceOf(DataResult.Success::class.java)
        assertThat((result as DataResult.Success).data.map { it.id }).containsExactly(1, 2)
    }

    @Test
    fun `does not call the asset source when the remote source succeeds`() = runTest {
        remote.setResult(listOf(validDto()))

        repository.getQuestions()

        assertThat(asset.callCount).isEqualTo(0)
    }

    @Test
    fun `falls back to the asset source when the remote source fails`() = runTest {
        remote.setFailure(IOException("no network"))
        asset.setResult(listOf(validDto(9)))

        val result = repository.getQuestions()

        assertThat(result).isInstanceOf(DataResult.Success::class.java)
        assertThat((result as DataResult.Success).data.single().id).isEqualTo(9)
        assertThat(asset.callCount).isEqualTo(1)
    }

    @Test
    fun `maps to Network error when both sources fail with an IOException`() = runTest {
        remote.setFailure(IOException("no network"))
        asset.setFailure(IOException("asset also unreadable"))

        val result = repository.getQuestions()

        assertThat(result).isEqualTo(DataResult.Error(AppError.Network))
    }

    @Test
    fun `maps to ServerError when both sources fail with an HttpException`() = runTest {
        remote.setFailure(httpException(500))
        asset.setFailure(httpException(503))

        val result = repository.getQuestions()

        assertThat(result).isEqualTo(DataResult.Error(AppError.ServerError))
    }

    @Test
    fun `maps to Unknown error for an unexpected exception type`() = runTest {
        remote.setFailure(IllegalStateException("boom"))
        asset.setFailure(IllegalStateException("boom too"))

        val result = repository.getQuestions()

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
        assertThat((result as DataResult.Error).error).isInstanceOf(AppError.Unknown::class.java)
    }

    @Test
    fun `maps to a Mapping error when the remote data is malformed`() = runTest {
        remote.setResult(listOf(QuestionDto(1, "Q1", listOf("A", "B"), 0))) // only 2 options

        val result = repository.getQuestions()

        assertThat(result).isInstanceOf(DataResult.Error::class.java)
        assertThat((result as DataResult.Error).error).isInstanceOf(AppError.Mapping::class.java)
    }

    @Test
    fun `maps to a Mapping error when the remote returns an empty list`() = runTest {
        remote.setResult(emptyList())

        val result = repository.getQuestions()

        assertThat(result).isEqualTo(DataResult.Error(AppError.Mapping("No questions were returned.")))
    }

    @Test
    fun `unicode question text survives the full remote-to-domain mapping`() = runTest {
        remote.setResult(
            listOf(QuestionDto(1, "Tap – reveal…?", listOf("A–B", "C", "D", "E"), 0)),
        )

        val result = repository.getQuestions() as DataResult.Success

        assertThat(result.data.single().text).isEqualTo("Tap – reveal…?")
    }
}
