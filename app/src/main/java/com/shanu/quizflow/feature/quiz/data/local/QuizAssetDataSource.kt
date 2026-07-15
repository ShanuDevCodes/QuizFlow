package com.shanu.quizflow.feature.quiz.data.local

import android.content.Context
import com.shanu.quizflow.feature.quiz.data.remote.dto.QuestionDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

interface QuizAssetDataSource {
    suspend fun getQuestions(): List<QuestionDto>
}

class QuizAssetDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) : QuizAssetDataSource {

    override suspend fun getQuestions(): List<QuestionDto> =
        context.assets.open(ASSET_FILE_NAME).use { stream ->
            json.decodeFromStream(stream)
        }

    companion object {
        const val ASSET_FILE_NAME = "questions.json"
    }
}
