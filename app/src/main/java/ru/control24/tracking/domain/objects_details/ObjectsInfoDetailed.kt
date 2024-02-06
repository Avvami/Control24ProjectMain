package ru.control24.tracking.domain.objects_details

import java.util.Date

data class ObjectsInfoDetailed(
    val objects: List<ObjectDetails>
)

data class ObjectDetails(
    val id: Int,
    val time: Date,
    val lat: Double,
    val long: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int
)
