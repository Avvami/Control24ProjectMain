package com.example.control24projectmain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object HttpGeocoder {

    suspend fun makeHttp(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Referer", BuildConfig.YANDEX_GEOCODE_API_KEY)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.code == 400) {
                throw HttpRequestHelper.BadRequestException(responseBody)
            } else {
                response.isSuccessful
                responseBody
            }
        }
    }
}