package com.example.simplechat.core.coreapi.utils

interface Transformation {

    fun <T, R> transform(model: T): R
}