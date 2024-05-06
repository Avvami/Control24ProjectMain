package ru.control24.tracking.monitoring.domain.objects.repository

import ru.control24.tracking.core.util.Resource

interface ObjectsRepository {
    suspend fun getObjects(login: String, password: String): Resource<String>
}