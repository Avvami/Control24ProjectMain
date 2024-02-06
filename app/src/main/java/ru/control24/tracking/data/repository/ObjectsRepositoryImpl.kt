package ru.control24.tracking.data.repository

import android.content.Context
import retrofit2.HttpException
import ru.control24.tracking.R
import ru.control24.tracking.data.mappers.toObjectsInfo
import ru.control24.tracking.data.remote.objects.ObjectsApi
import ru.control24.tracking.domain.objects.ObjectsInfo
import ru.control24.tracking.domain.repository.ObjectsRepository
import ru.control24.tracking.domain.util.Resource
import ru.control24.tracking.domain.util.UiText
import java.io.IOException

class ObjectsRepositoryImpl(
    private val api: ObjectsApi,
    private val context: Context
): ObjectsRepository {
    override suspend fun getObjects(login: String, password: String): Resource<ObjectsInfo> {
        return try {
            Resource.Success(
                data = api.getObjects(
                    login = login,
                    password = password
                ).toObjectsInfo()
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