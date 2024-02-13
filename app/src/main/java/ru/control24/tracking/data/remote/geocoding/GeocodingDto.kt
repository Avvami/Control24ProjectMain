package ru.control24.tracking.data.remote.geocoding

import com.squareup.moshi.Json

data class GeocodingDto(
    @field:Json(name = "display_name")
    val address: String?
)