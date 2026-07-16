package com.shanu.quizflow.feature.quiz.domain.model

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.feature.quiz.domain.sampleSession
import org.junit.Test

class QuizSessionTest {

    @Test
    fun `currentQuestion returns the question at currentIndex`() {
        val session = sampleSession()
        assertThat(session.currentQuestion).isEqualTo(session.questions[0])
    }

    @Test
    fun `currentQuestion is null once currentIndex reaches the end`() {
        val session = sampleSession(count = 10).copy(currentIndex = 10)
        assertThat(session.currentQuestion).isNull()
    }

    @Test
    fun `total reflects the number of questions`() {
        assertThat(sampleSession(count = 10).total).isEqualTo(10)
    }

    @Test
    fun `isFinished is false before the last question`() {
        val session = sampleSession(count = 10).copy(currentIndex = 9)
        assertThat(session.isFinished).isFalse()
    }

    @Test
    fun `isFinished is true once currentIndex reaches total`() {
        val session = sampleSession(count = 10).copy(currentIndex = 10)
        assertThat(session.isFinished).isTrue()
    }

    @Test
    fun `isStreakActive is false below the threshold`() {
        val session = sampleSession().copy(currentStreak = 2)
        assertThat(session.isStreakActive).isFalse()
    }

    @Test
    fun `isStreakActive is true at exactly the threshold`() {
        val session = sampleSession().copy(currentStreak = QuizSession.STREAK_BADGE_THRESHOLD)
        assertThat(session.isStreakActive).isTrue()
    }

    @Test
    fun `isStreakActive stays true above the threshold`() {
        val session = sampleSession().copy(currentStreak = QuizSession.STREAK_BADGE_THRESHOLD + 5)
        assertThat(session.isStreakActive).isTrue()
    }

    @Test
    fun `questions list is empty produces a finished session with no current question`() {
        val session = QuizSession(questions = emptyList())
        assertThat(session.isFinished).isTrue()
        assertThat(session.currentQuestion).isNull()
        assertThat(session.total).isEqualTo(0)
    }
}
