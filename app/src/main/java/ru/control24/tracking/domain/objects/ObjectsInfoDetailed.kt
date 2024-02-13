package ru.control24.tracking.domain.objects

data class ObjectsInfoDetailed(
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