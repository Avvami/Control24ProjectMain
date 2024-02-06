package ru.control24.tracking.di

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.control24.tracking.data.remote.auth.AuthApi
import ru.control24.tracking.data.remote.objects.ObjectsDetailsApi
import ru.control24.tracking.data.repository.AuthRepositoryImpl
import ru.control24.tracking.data.repository.ObjectsDetailsRepositoryImpl
import ru.control24.tracking.domain.repository.AuthRepository
import ru.control24.tracking.domain.repository.ObjectsDetailsRepository
import ru.control24.tracking.domain.util.C

interface AppModule {
    val authApi: AuthApi
    val authRepository: AuthRepository
    val objectDetailsApi: ObjectsDetailsApi
    val objectsDetailsRepository: ObjectsDetailsRepository
}

class AppModuleImpl(private val appContext: Context): AppModule {

    override val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(C.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    override val objectDetailsApi: ObjectsDetailsApi by lazy {
        Retrofit.Builder()
            .baseUrl(C.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }
    override val objectsDetailsRepository: ObjectsDetailsRepository by lazy {
        ObjectsDetailsRepositoryImpl(objectDetailsApi)
    }
}