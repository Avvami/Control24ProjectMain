package ru.control24.tracking.data.mappers

import android.content.Context
import android.text.format.DateFormat
import ru.control24.tracking.data.remote.objects.ObjectDetailsDto
import ru.control24.tracking.data.remote.objects.ObjectDto
import ru.control24.tracking.data.remote.objects.ObjectsDetailsDto
import ru.control24.tracking.data.remote.objects.ObjectsDto
import ru.control24.tracking.domain.objects.Object
import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.objects.ObjectDetails
import ru.control24.tracking.domain.objects.ObjectsInfoDetailed
import ru.control24.tracking.domain.util.CarCategory
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

fun ObjectsDetailsDto.toObjectsInfoDetailed(context: Context): ObjectsInfoDetailed {
    return ObjectsInfoDetailed(
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