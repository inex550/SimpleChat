package com.example.simplechat.core.ui.error

import android.content.Context
import com.example.simplechat.R
import com.example.simplechat.core.error.ErrorHandler
import com.example.simplechat.core.error.ErrorWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UiErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) {

    fun proceedError(throwable: Throwable, errorListener: (String) -> Unit = {}) {

        when (val error = errorHandler.getError(throwable)) {
            ErrorWrapper.ServerError -> errorListener(context.getString(R.string.error_something_broke))
            ErrorWrapper.ValidationError -> errorListener(context.getString(R.string.error_something_broke))
            ErrorWrapper.Unknown -> errorListener(context.getString(R.string.error_unknown))
            ErrorWrapper.TimeoutError -> errorListener(context.getString(R.string.error_timeout))
            is ErrorWrapper.CustomError -> errorListener(error.message)
        }
    }
}