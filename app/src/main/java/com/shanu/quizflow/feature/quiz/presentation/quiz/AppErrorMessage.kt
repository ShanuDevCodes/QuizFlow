package com.shanu.quizflow.feature.quiz.presentation.quiz

import androidx.annotation.StringRes
import com.shanu.quizflow.R
import com.shanu.quizflow.core.result.AppError

data class AppErrorMessage(@StringRes val messageRes: Int, val formatArg: String? = null)

fun AppError.toMessage(): AppErrorMessage = when (this) {
    AppError.Network -> AppErrorMessage(R.string.error_network)
    AppError.ServerError -> AppErrorMessage(R.string.error_server)
    is AppError.Mapping -> AppErrorMessage(R.string.error_mapping, reason)
    is AppError.Unknown -> AppErrorMessage(R.string.error_unknown)
}
