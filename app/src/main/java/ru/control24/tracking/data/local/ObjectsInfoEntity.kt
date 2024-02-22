package ru.control24.tracking.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ObjectsInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val objectId: Int,
    val username: String? = null,
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