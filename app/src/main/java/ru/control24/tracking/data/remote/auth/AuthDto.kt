package ru.control24.tracking.data.remote.auth

import com.squareup.moshi.Json

data class AuthDto(
    val key: String,
    @field:Json(name = "objects")
    val trackingObjects: List<ObjectDto>
)
