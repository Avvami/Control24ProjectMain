package ru.control24.tracking.data.mappers

import ru.control24.tracking.data.remote.auth.AuthDto
import ru.control24.tracking.data.remote.auth.ObjectDto
import ru.control24.tracking.domain.auth.AuthInfo
import ru.control24.tracking.domain.objects.ObjectInfo
import ru.control24.tracking.domain.util.CarCategory

fun AuthDto.toAuthInfo(): AuthInfo {
    return AuthInfo(
        key = key,
        trackingObjects = trackingObjects.map {
            it.toObjectInfo()
        }
    )
}

fun ObjectDto.toObjectInfo(): ObjectInfo {
    return ObjectInfo(
        id = id,
        name = name,
        category = CarCategory.fromDigit(category),
        client = client,
        licencePlate = licencePlate,
        carModel = carModel
    )
}