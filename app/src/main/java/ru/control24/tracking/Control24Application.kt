package ru.control24.tracking

import android.app.Application
import ru.control24.tracking.di.AppModule
import ru.control24.tracking.di.AppModuleImpl

class Control24Application: Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}