package ru.control24.tracking.data.mappers

import android.content.Context
import ru.control24.tracking.R
import ru.control24.tracking.data.remote.geocoding.GeocodingDto
import ru.control24.tracking.domain.util.UiText

fun GeocodingDto.toGeocodingInfo(context: Context): String {
    return if (address.isNullOrEmpty()) UiText.StringResource(R.string.geocoding_error).asString(context) else address
}