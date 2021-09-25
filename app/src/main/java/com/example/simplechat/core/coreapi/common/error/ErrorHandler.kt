package com.example.simplechat.core.coreapi.common.error

import com.example.simplechat.core.coreimpl.common.error.ErrorWrapper

interface ErrorHandler {

    fun getError(throwable: Throwable): ErrorWrapper
}