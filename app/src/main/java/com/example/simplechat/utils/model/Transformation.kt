package com.example.simplechat.utils.model

interface Transformation {

    fun <T, R> transform(model: T): R
}