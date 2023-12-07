package ru.control24.tracking.di

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.control24.tracking.data.remote.auth.AuthApi
import ru.control24.tracking.data.repository.AuthRepositoryImpl
import ru.control24.tracking.domain.repository.AuthRepository

interface AppModule {
    val authApi: AuthApi
    val authRepository: AuthRepository
}

class AppModuleImpl(private val appContext: Context): AppModule {
    override val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://91.193.225.170:8012/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }
}