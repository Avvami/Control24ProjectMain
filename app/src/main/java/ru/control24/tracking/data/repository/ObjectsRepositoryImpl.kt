package ru.control24.tracking.data.repository

import android.content.Context
import retrofit2.HttpException
import ru.control24.tracking.R
import ru.control24.tracking.data.mappers.toGeocodingInfo
import ru.control24.tracking.data.mappers.toObjectsInfo
import ru.control24.tracking.data.mappers.toObjectsInfoDetailed
import ru.control24.tracking.data.remote.geocoding.GeocodingApi
import ru.control24.tracking.data.remote.object_details.ObjectsDetailsApi
import ru.control24.tracking.data.remote.objects.ObjectsApi
import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.repository.ObjectsRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.domain.util.UiText
import java.io.IOException

class ObjectsRepositoryImpl(
    private val objectsApi: ObjectsApi,
    private val objectsDetailsApi: ObjectsDetailsApi,
    private val geocodingApi: GeocodingApi,
    private val context: Context
): ObjectsRepository {
    override suspend fun getObjects(login: String, password: String): Resource<ObjectsInfo> {
        return try {
            val objectsInfo = objectsApi.getObjects(
                login = login,
                password = password
            ).toObjectsInfo()

            val objectsDetails = objectsDetailsApi.getObjectsDetails(key = objectsInfo.key)
                .toObjectsInfoDetailed(context).objects.map { detail ->
                    var address = ""
                    try {
                        address = geocodingApi.getAddressFromLatLon(detail.lat, detail.lon).toGeocodingInfo(context)
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

            Resource.Success(
                data = objectsInfoWithDetails
            )
        } catch (e: HttpException) {
            e.printStackTrace()
            when (e.code()) {
                400 -> {
                    Resource.Error(e.message ?: UiText.StringResource(R.string.http_400).asString(context))
                }
                else -> {
                    Resource.Error(e.message ?: UiText.StringResource(R.string.unknown_error).asString(context))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(UiText.StringResource(R.string.api_call_error).asString(context))
        }
    }
}