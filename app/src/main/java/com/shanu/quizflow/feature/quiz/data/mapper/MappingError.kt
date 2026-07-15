package com.shanu.quizflow.feature.quiz.data.mapper

sealed class MappingError(message: String) : Exception(message) {
    data class InvalidOptionCount(val questionId: Int, val count: Int) :
        MappingError("Question $questionId has $count options; exactly 4 are required.")

    data class InvalidCorrectIndex(val questionId: Int, val index: Int) :
        MappingError("Question $questionId has correctOptionIndex $index, which is out of bounds.")
}
