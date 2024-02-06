package ru.control24.tracking.data.remote.objects

import com.squareup.moshi.Json

data class ObjectsDto(
    val key: String,
    @field:Json(name = "objects")
    val trackingObjects: List<ObjectDto>
)
