package com.example.control24projectmain

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object HttpRequestHelper {

    suspend fun makeHttpRequest(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.code == 400) {
                throw BadRequestException(responseBody)
            } else {
                response.isSuccessful
                responseBody
            }
        }
    }
    class BadRequestException(message: String) : IOException(message)
}