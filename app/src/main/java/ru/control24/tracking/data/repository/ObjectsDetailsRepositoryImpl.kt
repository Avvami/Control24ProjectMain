package ru.control24.tracking.data.repository

import ru.control24.tracking.data.mappers.toObjectsInfoDetailed
import ru.control24.tracking.data.remote.objects.ObjectsDetailsApi
import ru.control24.tracking.domain.objects.ObjectsInfoDetailed
import ru.control24.tracking.domain.repository.ObjectsDetailsRepository
import ru.control24.tracking.domain.util.Resource

class ObjectsDetailsRepositoryImpl(
    private val api: ObjectsDetailsApi
): ObjectsDetailsRepository {
    override suspend fun getObjectsDetails(key: String): Resource<ObjectsInfoDetailed> {
        return try {
            Resource.Success(
                data = api.getObjectsDetails(key = key).toObjectsInfoDetailed()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}