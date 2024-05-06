package ru.control24.tracking.monitoring.data.objects.remote

import com.squareup.moshi.Json

data class ObjectsDetailsDto(
    val objects: List<ObjectDetailsDto>
)

data class ObjectDetailsDto(
    val id: Int,
    @field:Json(name = "gmt")
    val gmtTime: String,
    val lat: Double,
    val lon: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int
)