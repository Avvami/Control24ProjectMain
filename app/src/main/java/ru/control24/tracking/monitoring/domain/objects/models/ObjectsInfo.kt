package ru.control24.tracking.monitoring.domain.objects.models

import ru.control24.tracking.monitoring.domain.objects.util.CarCategory

data class ObjectsInfo(
    val objectId: Int,
    val username: String,
    val name: String,
    val category: CarCategory,
    val client: String,
    val licencePlate: String,
    val carModel: String,
    val time: String?,
    val lat: Double?,
    val lon: Double?,
    val speed: Int?,
    val heading: Int?,
    val gps: Int?,
    val address: String?,
    val driver: String? = null,
    val driverNumber: String? = null,
    val displayOnMap: Boolean = false
)