package ru.control24.tracking.data.mappers

import android.content.Context
import android.text.format.DateFormat
import ru.control24.tracking.data.remote.object_details.ObjectDetailsDto
import ru.control24.tracking.data.remote.object_details.ObjectsDetailsDto
import ru.control24.tracking.domain.objects_details.ObjectDetails
import ru.control24.tracking.domain.objects_details.ObjectsInfoDetailed
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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