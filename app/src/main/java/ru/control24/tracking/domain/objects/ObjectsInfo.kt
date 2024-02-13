package ru.control24.tracking.domain.objects

import ru.control24.tracking.domain.objects_details.ObjectDetails
import ru.control24.tracking.domain.util.CarCategory

data class ObjectsInfo(
    val key: String,
    val trackingObjects: List<Object>
)

data class Object(
    val id: Int,
    val name: String,
    val category: CarCategory,
    val client: String,
    val licencePlate: String,
    val carModel: String,
    val details: ObjectDetails? = null
)
