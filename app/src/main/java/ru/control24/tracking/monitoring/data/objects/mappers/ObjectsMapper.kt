package ru.control24.tracking.monitoring.data.objects.mappers

import android.content.Context
import android.text.format.DateFormat
import ru.control24.tracking.core.data.local.ObjectsInfoEntity
import ru.control24.tracking.monitoring.data.objects.remote.ObjectDetailsDto
import ru.control24.tracking.monitoring.data.objects.remote.ObjectDto
import ru.control24.tracking.monitoring.data.objects.remote.ObjectsDetailsDto
import ru.control24.tracking.monitoring.data.objects.remote.ObjectsDto
import ru.control24.tracking.monitoring.domain.objects.models.Object
import ru.control24.tracking.monitoring.domain.objects.models.Objects
import ru.control24.tracking.monitoring.domain.objects.models.ObjectDetails
import ru.control24.tracking.monitoring.domain.objects.models.ObjectsDetails
import ru.control24.tracking.monitoring.domain.objects.models.ObjectsInfo
import ru.control24.tracking.monitoring.domain.objects.util.CarCategory
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun ObjectsDto.toObjects(username: String): Objects {
    return Objects(
        key = key,
        trackingObjects = trackingObjects.map {
            it.toObject(username)
        }
    )
}

fun ObjectDto.toObject(username: String): Object {
    return Object(
        id = id,
        username = username,
        name = name,
        category = category,
        client = client,
        licencePlate = licencePlate,
        carModel = carModel
    )
}

fun ObjectsDetailsDto.toObjectsDetails(context: Context): ObjectsDetails {
    return ObjectsDetails(
        objects = objects.map {
            it.toObjectDetails(context)
        }
    )
}

fun ObjectDetailsDto.toObjectDetails(context: Context): ObjectDetails {
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val pattern = if (is24HourFormat) "dd.MM.yyyy H:mm:ss" else "dd.MM.yyyy h:mm:ss a"

    val gmtFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss", Locale.getDefault())
    val zonedDateTime = ZonedDateTime.parse(gmtTime, gmtFormatter.withZone(ZoneId.of("Z")))

    val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    val localFormatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val formattedTime = localDateTime.format(localFormatter)

    return ObjectDetails(
        id = id,
        time = formattedTime,
        lat = lat,
        lon = lon,
        speed = speed,
        heading = heading,
        gps = gps
    )
}

fun Object.toObjectsInfoEntity(): ObjectsInfoEntity {
    return ObjectsInfoEntity(
        objectId = id,
        username = username,
        name = name,
        category = category,
        client = client,
        licencePlate = licencePlate,
        carModel = carModel,
        time = details?.time,
        lat = details?.lat,
        lon = details?.lon,
        speed = details?.speed,
        heading = details?.heading,
        gps = details?.gps,
        address = details?.address
    )
}

fun ObjectsInfoEntity.toObjectsInfo(): ObjectsInfo {
    return ObjectsInfo(
        objectId = objectId,
        username = username,
        name = name,
        category = CarCategory.fromDigit(category),
        client = client,
        licencePlate = licencePlate,
        carModel = carModel,
        time = time,
        lat = lat,
        lon = lon,
        speed = speed,
        heading = heading,
        gps = gps,
        address = address
    )
}