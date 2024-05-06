package ru.control24.tracking.monitoring.data.geocoding.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("reverse?format=jsonv2&accept-language=ru")
    suspend fun getAddressFromLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): GeocodingDto
}