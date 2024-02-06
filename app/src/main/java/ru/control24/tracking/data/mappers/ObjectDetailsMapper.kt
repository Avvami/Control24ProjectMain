package ru.control24.tracking.data.mappers

import ru.control24.tracking.data.remote.object_details.ObjectDetailsDto
import ru.control24.tracking.data.remote.object_details.ObjectsDetailsDto
import ru.control24.tracking.domain.objects_details.ObjectDetails
import ru.control24.tracking.domain.objects_details.ObjectsInfoDetailed
import java.text.SimpleDateFormat
import java.util.Locale

fun ObjectsDetailsDto.toObjectsInfoDetailed(): ObjectsInfoDetailed {
    return ObjectsInfoDetailed(
        objects = objects.map {
            it.toObjectDetails()
        }
    )
}

fun ObjectDetailsDto.toObjectDetails(): ObjectDetails {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    val date = formatter.parse(time)!!
    return ObjectDetails(
        id = id,
        time = date,
        lat = lat,
        long = long,
        speed = speed,
        heading = heading,
        gps = gps
    )
}