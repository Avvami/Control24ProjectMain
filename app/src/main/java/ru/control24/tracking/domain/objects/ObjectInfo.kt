package ru.control24.tracking.domain.objects

import ru.control24.tracking.domain.util.CarCategory

data class ObjectInfo(
    val id: Int,
    val name: String,
    val category: CarCategory,
    val client: String,
    val licencePlate: String,
    val carModel: String
)
