package ru.control24.tracking.monitoring.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.control24.tracking.monitoring.domain.objects.repository.ObjectsRepository
import ru.control24.tracking.monitoring.data.objects.repository.ObjectsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindObjectsRepository(
        objectsRepositoryImpl: ObjectsRepositoryImpl
    ): ObjectsRepository
}