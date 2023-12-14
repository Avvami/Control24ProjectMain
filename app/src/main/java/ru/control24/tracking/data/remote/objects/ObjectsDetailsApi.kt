package ru.control24.tracking.data.remote.objects

import retrofit2.http.GET
import retrofit2.http.Path

interface ObjectsDetailsApi {
    @GET("update2&{key}")
    suspend fun getObjectsDetails(
        @Path("key") key: String
    ): ObjectsDetailsDto
}