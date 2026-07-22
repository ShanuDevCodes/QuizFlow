package com.shanu.quizflow.feature.quiz.domain

import com.shanu.quizflow.core.result.AppError
import com.shanu.quizflow.core.result.DataResult
import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.model.Subject
import com.shanu.quizflow.feature.quiz.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeQuizRepository(
    private var result: DataResult<List<Question>> = DataResult.Success(sampleQuestions()),
    private val subjectsFlow: MutableStateFlow<List<Subject>> = MutableStateFlow(sampleSubjects()),
) : QuizRepository {

    var getQuestionsCallCount: Int = 0
        private set

    fun setResult(result: DataResult<List<Question>>) {
        this.result = result
    }

    fun setSubjects(subjects: List<Subject>) {
        this.subjectsFlow.value = subjects
    }

    override fun observeSubjects(): Flow<List<Subject>> = subjectsFlow

    override suspend fun syncSubjects(): DataResult<Unit> = DataResult.Success(Unit)

    override suspend fun getQuestions(subjectId: String): DataResult<List<Question>> {
        getQuestionsCallCount++
        return result
    }
}

fun errorResult(error: AppError = AppError.Network): DataResult<List<Question>> = DataResult.Error(error)

fun sampleSubjects(): List<Subject> = listOf(
    Subject("android_basics", "Android Basics", "Fundamentals of Android development", "url1"),
    Subject("jetpack_compose", "Jetpack Compose", "Modern UI toolkit for Android", "url2"),
)
