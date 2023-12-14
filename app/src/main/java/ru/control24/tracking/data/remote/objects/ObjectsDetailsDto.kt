package ru.control24.tracking.data.remote.objects

import com.squareup.moshi.Json

data class ObjectsDetailsDto(
    val objects: List<ObjectDetailsDto>
)

data class ObjectDetailsDto(
    val id: Int,
    @field:Json(name = "gmt")
    val time: String,
    val lat: Double,
    @field:Json(name = "lon")
    val long: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int
)