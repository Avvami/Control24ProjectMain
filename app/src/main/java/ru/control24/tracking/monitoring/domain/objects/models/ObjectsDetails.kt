package ru.control24.tracking.monitoring.domain.objects.models

data class ObjectsDetails(
    val objects: List<ObjectDetails>
)

data class ObjectDetails(
    val id: Int,
    val time: String,
    val lat: Double,
    val lon: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int,
    val address: String? = null
)