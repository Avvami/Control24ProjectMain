package ru.control24.tracking.domain.objects

data class ObjectInfo(
    val id: Int,
    val name: String,
    val client: String,
    val licencePlate: String,
    val carModel: String
)
