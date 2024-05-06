package ru.control24.tracking.monitoring.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.control24.tracking.core.util.C
import ru.control24.tracking.monitoring.data.geocoding.remote.GeocodingApi
import ru.control24.tracking.monitoring.data.objects.remote.ObjectsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MonitoringModule {

    @Provides
    @Singleton
    fun provideObjectsApi(): ObjectsApi {
        return Retrofit.Builder()
            .baseUrl(C.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(): GeocodingApi {
        return Retrofit.Builder()
            .baseUrl(C.GEOCODE_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }
}