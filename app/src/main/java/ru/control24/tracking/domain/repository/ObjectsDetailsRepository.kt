package ru.control24.tracking.domain.repository

import ru.control24.tracking.domain.objects.ObjectsInfoDetailed
import ru.control24.tracking.domain.util.Resource

interface ObjectsDetailsRepository {
    suspend fun getObjectsDetails(key: String): Resource<ObjectsInfoDetailed>
}