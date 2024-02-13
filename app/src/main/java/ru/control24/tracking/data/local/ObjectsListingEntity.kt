package ru.control24.tracking.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.control24.tracking.domain.util.CarCategory
import java.time.LocalDateTime

@Entity(tableName = "tracking_objects")
data class ObjectsListingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val objectId: Int,
    val name: String,
    val category: CarCategory,
    val client: String,
    val licencePlate: String,
    val carModel: String,
    val time: LocalDateTime,
    val lat: Double,
    val long: Double,
    val speed: Int,
    val heading: Int,
    val gps: Int,
    val driver: String
)
