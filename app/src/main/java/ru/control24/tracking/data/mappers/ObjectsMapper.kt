package ru.control24.tracking.data.mappers

import ru.control24.tracking.data.remote.objects.ObjectDto
import ru.control24.tracking.data.remote.objects.ObjectsDto
import ru.control24.tracking.domain.objects.Object
import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.util.CarCategory

fun ObjectsDto.toObjectsInfo(): ObjectsInfo {
    return ObjectsInfo(
        key = key,
        trackingObjects = trackingObjects.map {
            it.toObject()
        }
    )
}

fun ObjectDto.toObject(): Object {
    return Object(
        id = id,
        name = name,
        category = CarCategory.fromDigit(category),
        client = client,
        licencePlate = licencePlate,
        carModel = carModel
    )
}