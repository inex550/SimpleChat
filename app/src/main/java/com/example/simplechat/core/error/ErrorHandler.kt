package com.example.simplechat.core.error

import com.example.simplechat.core.network.error.NetworkErrorParser
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ErrorHandler @Inject constructor(
    private val errorParser: NetworkErrorParser
) {

    fun getError(throwable: Throwable): ErrorWrapper = when(throwable) {
        is HttpException -> when (throwable.code()) {
            422 -> ErrorWrapper.ValidationError
            in 400..499 -> {
                val response = throwable.response()?.errorBody()?.string()
                val errorModel = errorParser.parseError(response)

                errorModel?.detail?.let { detail ->
                    ErrorWrapper.CustomError(detail)
                } ?: ErrorWrapper.Unknown
            }
            in 500..599 -> ErrorWrapper.ServerError
            else -> ErrorWrapper.Unknown
        }
        is SocketTimeoutException -> ErrorWrapper.TimeoutError
        else -> ErrorWrapper.Unknown
    }
}