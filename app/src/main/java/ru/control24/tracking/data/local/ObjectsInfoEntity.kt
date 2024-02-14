package ru.control24.tracking.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.control24.tracking.domain.util.CarCategory

@Entity
data class ObjectsInfoEntity(
    @PrimaryKey val id: Int,
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
    val driver: String?,
    val driverNumber: String?,
    val displayOnMap: Boolean = false
)