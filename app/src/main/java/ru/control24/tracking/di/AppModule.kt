package ru.control24.tracking.di

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.control24.tracking.data.remote.geocoding.GeocodingApi
import ru.control24.tracking.data.remote.objects.ObjectsApi
import ru.control24.tracking.data.repository.ObjectsRepositoryImpl
import ru.control24.tracking.domain.repository.ObjectsRepository
import ru.control24.tracking.domain.util.C

interface AppModule {
    val objectsApi: ObjectsApi
    val objectsRepository: ObjectsRepository
    val geocodingApi: GeocodingApi
}

class AppModuleImpl(private val appContext: Context): AppModule {

    override val objectsApi: ObjectsApi by lazy {
        Retrofit.Builder()
            .baseUrl(C.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }
    override val objectsRepository: ObjectsRepository by lazy {
        ObjectsRepositoryImpl(
            objectsApi = objectsApi,
            geocodingApi = geocodingApi,
            context = appContext
        )
    }

    override val geocodingApi: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(C.GEOCODE_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }
}