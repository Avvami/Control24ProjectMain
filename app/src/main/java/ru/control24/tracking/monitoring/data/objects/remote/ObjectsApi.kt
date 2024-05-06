package ru.control24.tracking.monitoring.data.objects.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ObjectsApi {
    @GET("login2&{login}&{password}")
    suspend fun getObjects(
        @Path("login") login: String,
        @Path("password") password: String
    ): ObjectsDto

    @GET("update2&{key}")
    suspend fun getObjectsDetails(
        @Path("key") key: String
    ): ObjectsDetailsDto
}