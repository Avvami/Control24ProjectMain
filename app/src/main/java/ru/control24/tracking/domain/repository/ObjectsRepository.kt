package ru.control24.tracking.domain.repository

import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.util.Resource

interface ObjectsRepository {
    suspend fun getObjects(login: String, password: String): Resource<ObjectsInfo>
}