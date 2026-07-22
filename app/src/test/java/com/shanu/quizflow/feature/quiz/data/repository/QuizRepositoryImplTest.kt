package com.shanu.quizflow.feature.quiz.data.repository

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.core.coroutines.FakeDispatcherProvider
import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.data.local.dao.QuestionDao
import com.shanu.quizflow.feature.quiz.data.local.dao.SubjectDao
import com.shanu.quizflow.feature.quiz.data.local.entity.QuestionEntity
import com.shanu.quizflow.feature.quiz.data.local.entity.SubjectEntity
import com.shanu.quizflow.feature.quiz.data.mapper.QuestionMapper
import com.shanu.quizflow.feature.quiz.data.mapper.SubjectMapper
import com.shanu.quizflow.feature.quiz.data.remote.FakeQuizRemoteDataSource
import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import com.shanu.quizflow.feature.quiz.data.remote.dto.SubjectDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class FakeSubjectDao : SubjectDao {
    val subjects = mutableListOf<SubjectEntity>()
    private val flow = MutableStateFlow<List<SubjectEntity>>(emptyList())

    override fun observeAll(): Flow<List<SubjectEntity>> = flow
    override suspend fun getAll(): List<SubjectEntity> = subjects
    override suspend fun upsertAll(subjects: List<SubjectEntity>) {
        this.subjects.clear()
        this.subjects.addAll(subjects)
        flow.value = subjects
    }
    override suspend fun count(): Int = subjects.size
}

class FakeQuestionDao : QuestionDao {
    val questions = mutableListOf<QuestionEntity>()

    override suspend fun getBySubjectId(subjectId: String): List<QuestionEntity> =
        questions.filter { it.subjectId == subjectId }

    override suspend fun upsertAll(questions: List<QuestionEntity>) {
        this.questions.addAll(questions)
    }

    override suspend fun countBySubject(subjectId: String): Int =
        questions.count { it.subjectId == subjectId }

    override suspend fun deleteBySubjectId(subjectId: String) {
        questions.removeAll { it.subjectId == subjectId }
    }
}

class QuizRepositoryImplTest {

    private val remote = FakeQuizRemoteDataSource()
    private val subjectDao = FakeSubjectDao()
    private val questionDao = FakeQuestionDao()
    private val repository = QuizRepositoryImpl(
        remoteDataSource = remote,
        subjectDao = subjectDao,
        questionDao = questionDao,
        subjectMapper = SubjectMapper(),
        questionMapper = QuestionMapper(),
        dispatcherProvider = FakeDispatcherProvider(Dispatchers.Unconfined),
    )

    private fun validDto(id: Int = 1) = QuestionDto(id, "Q$id", listOf("A", "B", "C", "D"), 0)

    @Test
    fun `syncSubjects fetches subjects and per-module questions into Room`() = runTest {
        remote.setSubjectsResult(
            listOf(SubjectDto("android_basics", "Android Basics", "Desc", "https://url1")),
        )
        remote.setQuestionsResult(listOf(validDto(1), validDto(2)))

        val result = repository.syncSubjects()

        assertThat(result).isEqualTo(DataResult.Success(Unit))
        assertThat(subjectDao.subjects).hasSize(1)
        assertThat(questionDao.questions).hasSize(2)
    }

    @Test
    fun `getQuestions returns cached questions from Room when available`() = runTest {
        questionDao.upsertAll(
            listOf(
                QuestionEntity(1, "android_basics", "Q1", "A", "B", "C", "D", 0),
            ),
        )

        val result = repository.getQuestions("android_basics")

        assertThat(result).isInstanceOf(DataResult.Success::class.java)
        assertThat((result as DataResult.Success).data.single().id).isEqualTo(1)
    }

    @Test
    fun `getQuestions maps to Network error when fetching fails and cache is empty`() = runTest {
        subjectDao.upsertAll(
            listOf(SubjectEntity("android_basics", "Title", "Desc", "https://url", 0)),
        )
        remote.setFailure(IOException("No network"))

        val result = repository.getQuestions("android_basics")

        assertThat(result).isEqualTo(DataResult.Error(AppError.Network))
    }
}
