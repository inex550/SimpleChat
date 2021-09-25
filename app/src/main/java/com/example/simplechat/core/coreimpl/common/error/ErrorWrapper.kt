package com.example.simplechat.core.coreimpl.common.error

sealed class ErrorWrapper {
    object Unknown: ErrorWrapper()
    object ValidationError: ErrorWrapper()
    object ServerError: ErrorWrapper()
    object TimeoutError: ErrorWrapper()
    data class CustomError(val message: String): ErrorWrapper()
}
