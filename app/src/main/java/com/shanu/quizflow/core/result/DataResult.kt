package com.shanu.quizflow.core.result

import retrofit2.HttpException
import java.io.IOException

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val error: AppError) : DataResult<Nothing>
}

inline fun <T, R> DataResult<T>.map(transform: (T) -> R): DataResult<R> = when (this) {
    is DataResult.Success -> DataResult.Success(transform(data))
    is DataResult.Error -> this
}

fun Throwable.toAppError(): AppError = when (this) {
    is HttpException -> AppError.ServerError
    is IOException -> AppError.Network
    else -> AppError.Unknown(this)
}