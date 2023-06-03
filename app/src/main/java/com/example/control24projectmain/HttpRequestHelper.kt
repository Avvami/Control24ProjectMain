package com.example.control24projectmain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.SocketTimeoutException

object HttpRequestHelper {

    suspend fun makeHttpRequest(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                if (response.code == 400) {
                    throw BadRequestException(responseBody)
                } else {
                    response.isSuccessful
                    responseBody
                }
            } catch (exception: SocketTimeoutException) {
                throw TimeoutException("Connection timed out", exception)
            }
        }
    }
    class BadRequestException(message: String) : IOException(message)
    class TimeoutException(message: String, cause: Throwable? = null) : IOException(message, cause)
}