package ru.control24.tracking.monitoring.data.geocoding.remote

import com.squareup.moshi.Json

data class GeocodingDto(
    @field:Json(name = "display_name")
    val address: String?
)