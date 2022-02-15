package com.example.simplechat.utils.extensions

import com.example.simplechat.core.network.di.NetworkModule

fun String.withBaseImageUrl(): String = NetworkModule.BASE_IMAGE_URL + this