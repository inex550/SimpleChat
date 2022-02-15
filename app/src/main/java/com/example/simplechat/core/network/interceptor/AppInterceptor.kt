package com.example.simplechat.core.network.interceptor

import com.example.simplechat.core.preference.UserPreferenceStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInterceptor @Inject constructor(
    private val prefs: UserPreferenceStorage
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (prefs.token != null) {
            val httpUrl = request.url.newBuilder()
                .addQueryParameter("token", prefs.token)
                .build()

            request = request.newBuilder().url(httpUrl).build()
        }

        return chain.proceed(request)
    }
}