package ru.control24.tracking.data.repository

import android.content.Context
import retrofit2.HttpException
import ru.control24.tracking.R
import ru.control24.tracking.data.mappers.toObjectsInfoDetailed
import ru.control24.tracking.data.remote.object_details.ObjectsDetailsApi
import ru.control24.tracking.domain.objects_details.ObjectsInfoDetailed
import ru.control24.tracking.domain.repository.ObjectsDetailsRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.domain.util.UiText
import java.io.IOException

class ObjectsDetailsRepositoryImpl(
    private val api: ObjectsDetailsApi,
    private val context: Context
): ObjectsDetailsRepository {
    override suspend fun getObjectsDetails(key: String): Resource<ObjectsInfoDetailed> {
        return try {
            Resource.Success(
                data = api.getObjectsDetails(key = key).toObjectsInfoDetailed(context)
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