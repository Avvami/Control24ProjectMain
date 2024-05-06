package ru.control24.tracking.monitoring.data.objects.repository

import android.app.Application
import retrofit2.HttpException
import ru.control24.tracking.R
import ru.control24.tracking.core.data.local.ObjectsDao
import ru.control24.tracking.core.data.local.UsersDao
import ru.control24.tracking.core.data.local.UsersEntity
import ru.control24.tracking.core.util.Resource
import ru.control24.tracking.core.util.UiText
import ru.control24.tracking.monitoring.data.geocoding.mappers.toGeocodingInfo
import ru.control24.tracking.monitoring.data.geocoding.remote.GeocodingApi
import ru.control24.tracking.monitoring.data.objects.mappers.toObjects
import ru.control24.tracking.monitoring.data.objects.mappers.toObjectsDetails
import ru.control24.tracking.monitoring.data.objects.mappers.toObjectsInfoEntity
import ru.control24.tracking.monitoring.data.objects.remote.ObjectsApi
import ru.control24.tracking.monitoring.domain.objects.repository.ObjectsRepository
import java.io.IOException
import javax.inject.Inject

class ObjectsRepositoryImpl @Inject constructor(
    private val objectsApi: ObjectsApi,
    private val geocodingApi: GeocodingApi,
    private val usersDao: UsersDao,
    private val objectsDao: ObjectsDao,
    private val appContext: Application
): ObjectsRepository {
    override suspend fun getObjects(login: String, password: String): Resource<String> {
        return try {
            val objectsInfo = objectsApi.getObjects(
                login = login,
                password = password
            ).toObjects()

            val objectsDetails = objectsApi.getObjectsDetails(key = objectsInfo.key)
                .toObjectsDetails(appContext).objects.map { detail ->
                    var address = ""
                    try {
                        address = geocodingApi.getAddressFromLatLon(detail.lat, detail.lon).toGeocodingInfo(appContext)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    detail.copy(address = address)
                }

            val trackingObjectsWithDetails = objectsInfo.trackingObjects.map { obj ->
                val details = objectsDetails.find { it.id == obj.id }
                obj.copy(details = details)
            }
            val objectsInfoWithDetails = objectsInfo.copy(trackingObjects = trackingObjectsWithDetails)

            usersDao.insertUser(
                UsersEntity(
                username = login,
                password = password,
                active = true
            )
            ).also {
                objectsDao.insertTrackingObjects(
                    objectsInfoWithDetails.trackingObjects.map { it.toObjectsInfoEntity().copy(username = login) }
                )
            }

            Resource.Success(
                data = objectsInfoWithDetails.key
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            when (e.code()) {
                400 -> {
                    Resource.Error(e.message ?: UiText.StringResource(R.string.http_400).asString(appContext))
                }
                else -> {
                    Resource.Error(e.message ?: UiText.StringResource(R.string.unknown_error).asString(appContext))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(UiText.StringResource(R.string.api_call_error).asString(appContext))
        }
    }
}