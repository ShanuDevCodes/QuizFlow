package com.shanu.quizflow.feature.quiz.presentation.quiz

import com.google.common.truth.Truth.assertThat
import com.shanu.quizflow.R
import com.shanu.quizflow.core.result.AppError
import org.junit.Test

class AppErrorMessageTest {

    @Test
    fun `Network maps to the network error resource with no format arg`() {
        val message = AppError.Network.toMessage()

        assertThat(message.messageRes).isEqualTo(R.string.error_network)
        assertThat(message.formatArg).isNull()
    }

    @Test
    fun `ServerError maps to the server error resource with no format arg`() {
        val message = AppError.ServerError.toMessage()

        assertThat(message.messageRes).isEqualTo(R.string.error_server)
        assertThat(message.formatArg).isNull()
    }

    @Test
    fun `Mapping carries its reason as the format arg`() {
        val message = AppError.Mapping("bad data").toMessage()

        assertThat(message.messageRes).isEqualTo(R.string.error_mapping)
        assertThat(message.formatArg).isEqualTo("bad data")
    }

    @Test
    fun `Unknown maps to the generic error resource with no format arg`() {
        val message = AppError.Unknown(RuntimeException("boom")).toMessage()

        assertThat(message.messageRes).isEqualTo(R.string.error_unknown)
        assertThat(message.formatArg).isNull()
    }
}
