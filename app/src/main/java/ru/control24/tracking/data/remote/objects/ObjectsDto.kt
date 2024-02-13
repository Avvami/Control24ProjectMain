package ru.control24.tracking.data.remote.objects

import com.squareup.moshi.Json

data class ObjectsDto(
    val key: String,
    @field:Json(name = "objects")
    val trackingObjects: List<ObjectDto>
)

data class ObjectDto(
    val id: Int,
    val name: String,
    val category: Int,
    val client: String,
    @field:Json(name = "avto_no")
    val licencePlate: String,
    @field:Json(name = "avto_model")
    val carModel: String
)