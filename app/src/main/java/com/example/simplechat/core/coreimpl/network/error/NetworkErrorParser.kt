package com.example.simplechat.core.coreimpl.network.error

import com.google.gson.Gson
import java.lang.Exception
import javax.inject.Inject

class NetworkErrorParser @Inject constructor(
    private val gson: Gson
) {
    fun parseError(response: String?) = try {
        gson.fromJson(response, ErrorNetModel::class.java)
    } catch (e: Exception) {
        null
    }
}