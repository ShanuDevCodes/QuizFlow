package com.shanu.quizflow.feature.quiz.domain

import com.shanu.quizflow.feature.quiz.domain.model.Question
import com.shanu.quizflow.feature.quiz.domain.model.QuizSession

fun sampleQuestions(count: Int = 10): List<Question> = (1..count).map { id ->
    Question(
        id = id,
        text = "Question $id",
        options = listOf("Option A", "Option B", "Option C", "Option D"),
        correctIndex = 0,
    )
}

fun sampleSession(count: Int = 10): QuizSession = QuizSession(questions = sampleQuestions(count))
