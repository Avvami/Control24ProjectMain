package ru.control24.tracking.domain.auth

import ru.control24.tracking.domain.objects.ObjectInfo

data class AuthInfo(
    val key: String,
    val trackingObjects: List<ObjectInfo>
)
