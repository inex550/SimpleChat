package com.example.simplechat.core.coreimpl.network.error

data class ErrorNetModel(
    // Validation Error
    val loc: List<String>?,
    val msg: String?,
    val type: String?,

    // Other
    val detail: String
)