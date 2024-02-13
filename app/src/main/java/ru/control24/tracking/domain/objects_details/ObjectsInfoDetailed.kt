package ru.control24.tracking.domain.objects_details

data class ObjectsInfoDetailed(
    val objects: List<ObjectDetails>
)

data class ObjectDetails(
    val id: Int,
    val time: String,
    val lat: Double,
    val long: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int
)
