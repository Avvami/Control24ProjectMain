package com.example.control24projectmain

import android.app.Application
import android.location.Geocoder
import android.text.format.DateFormat
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale


const val yandexMap = "YANDEX"
const val osmMap = "OSM"
const val SCHEME = "SCHEME"
const val SATELLITE = "SATELLITE"
const val HYBRID = "HYBRID"

class AppLevelClass : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set the api key to access yandex maps
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAP_API_KEY)

        // Load and set saved app theme
        when (UserManager.getThemeState(this@AppLevelClass)) {
            "OFF" -> themeChange(this@AppLevelClass, "OFF")
            "ON" -> themeChange(this@AppLevelClass, "ON")
            "SCHEDULED" -> themeChange(this@AppLevelClass, "SCHEDULED")
            "SYSTEM" -> themeChange(this@AppLevelClass, "SYSTEM")
        }

        // Initialize ThreeTenABP
        AndroidThreeTen.init(this@AppLevelClass)
    }

    fun convertTime(timeGMT: String): String {
        val formatter24h = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm:ss")
        val formatter12h = DateTimeFormatter.ofPattern("dd.MM.yyyy h:mm:ss a")
        val zonedTimeGMT = ZonedDateTime.parse(timeGMT, formatter24h.withZone(ZoneId.of("GMT")))

        val isSystem24Hour = DateFormat.is24HourFormat(this@AppLevelClass)

        // Get the system default time zone
        val defaultZoneId = ZoneId.systemDefault()

        val zonedTimeLocal = zonedTimeGMT.withZoneSameInstant(defaultZoneId)

        // Format the output
        return if (isSystem24Hour) {
            formatter24h.format(zonedTimeLocal)
        } else {
            formatter12h.format(zonedTimeLocal)
        }
    }

    suspend fun coroutineGeocode(latitude: Double, longitude: Double): String {
        var addressObject: String

        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(this@AppLevelClass, Locale("ru"))
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.get(0)
                val formattedAddress = StringBuilder()
                address?.let { it ->
                    it.countryName?.let { if (it.isNotBlank()) formattedAddress.append(it).append(", ") }
                    it.locality?.let { if (it.isNotBlank()) formattedAddress.append(it).append(", ") }
                    it.thoroughfare?.let { if (it.isNotBlank()) formattedAddress.append(it).append(", ") }
                    it.subThoroughfare?.let { if (it.isNotBlank()) formattedAddress.append(it) }

                    // Remove trailing comma and space if present
                    if (formattedAddress.endsWith(", ")) {
                        formattedAddress.setLength(formattedAddress.length - 2)
                    }
                }
                addressObject = formattedAddress.toString()
                Log.i("HDFJSDHFK", "Standard: $addressObject")
            } catch (e: Exception) {
                val httpGeocodeResponse = HttpGeocoder.makeHttp("https://geocode-maps.yandex.ru/1.x/?apikey=${BuildConfig.YANDEX_GEOCODE_API_KEY}&geocode=$longitude,$latitude&format=json")
                val jsonResponse = JSONObject(httpGeocodeResponse)
                addressObject = jsonResponse.getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .getJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getJSONObject("metaDataProperty")
                    .getJSONObject("GeocoderMetaData")
                    .getJSONObject("Address")
                    .getString("formatted")
                Log.i("HDFJSDHFK", "Yandex: $addressObject")
            }
        }
        return addressObject
    }
}