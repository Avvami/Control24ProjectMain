package ru.control24.tracking.data.remote.objects

import retrofit2.http.GET
import retrofit2.http.Path

interface ObjectsApi {
    @GET("login2&{login}&{password}")
    suspend fun getObjects(
        @Path("login") login: String,
        @Path("password") password: String
    ): ObjectsDto
}