package com.shanu.quizflow.core.result

sealed interface AppError {
    data object Network : AppError
    data object ServerError : AppError
    data class Mapping(val reason: String) : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}
