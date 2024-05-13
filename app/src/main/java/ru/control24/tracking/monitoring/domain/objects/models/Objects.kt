package ru.control24.tracking.monitoring.domain.objects.models

data class Objects(
    val key: String,
    val trackingObjects: List<Object>
)

data class Object(
    val id: Int,
    val username: String,
    val name: String,
    val category: Int,
    val client: String,
    val licencePlate: String,
    val carModel: String,
    val details: ObjectDetails? = null
)