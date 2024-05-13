package ru.control24.tracking.core.data.local

import androidx.room.Entity

@Entity(primaryKeys = ["objectId", "username"])
data class ObjectsInfoEntity(
    val objectId: Int,
    val username: String,
    val name: String,
    val category: Int,
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