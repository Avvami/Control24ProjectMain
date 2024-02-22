package ru.control24.tracking.domain.objects

data class Objects(
    val key: String,
    val trackingObjects: List<Object>
)

data class Object(
    val id: Int,
    val name: String,
    val category: Int,
    val client: String,
    val licencePlate: String,
    val carModel: String,
    val details: ObjectDetails? = null
)