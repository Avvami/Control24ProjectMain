package ru.control24.tracking.monitoring.data.geocoding.mappers

import android.content.Context
import ru.control24.tracking.R
import ru.control24.tracking.monitoring.data.geocoding.remote.GeocodingDto
import ru.control24.tracking.core.util.UiText

fun GeocodingDto.toGeocodingInfo(context: Context): String {
    return if (address.isNullOrEmpty()) UiText.StringResource(R.string.geocoding_error).asString(context) else address
}