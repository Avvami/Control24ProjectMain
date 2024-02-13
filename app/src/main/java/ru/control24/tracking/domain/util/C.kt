package ru.control24.tracking.domain.util

import android.util.Log

object C {
    var login = ""
        set(value) {
            when {
                value == "~reset" -> {
                    field = ""
                }
                value.isNotEmpty() -> {
                    field = value
                    Log.i(javaClass.name, "login setter: success, $value")
                }
                else -> {
                    Log.i(javaClass.name, "login setter: tried set with empty string")
                    Log.d(javaClass.name, "login setter: " + Throwable().stackTrace.contentToString())
                }
            }
        }
    
    var password = ""
        set(value) {
            when {
                value == "~reset" -> {
                    field = ""
                }
                value.isNotEmpty() -> {
                    field = value
                    Log.i(javaClass.name, "password setter: success, $value")
                }
                else -> {
                    Log.i(javaClass.name, "password setter: tried set with empty string")
                    Log.d(javaClass.name, "password setter: " + Throwable().stackTrace.contentToString())
                }
            }
        }

    fun clearData() {
        login = "~reset"
        password = "~reset"
    }

    const val LOGIN = "LOGIN"
    const val PASSWORD = "PASSWORD"
    
    const val BASE_URL = "http://91.193.225.170:8012/"
    const val GEOCODE_BASE_URL = "https://nominatim.openstreetmap.org/"
}