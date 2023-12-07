package ru.control24.tracking.data.repository

import ru.control24.tracking.data.mappers.toAuthInfo
import ru.control24.tracking.data.remote.auth.AuthApi
import ru.control24.tracking.domain.auth.AuthInfo
import ru.control24.tracking.domain.repository.AuthRepository
import ru.control24.tracking.domain.util.Resource

class AuthRepositoryImpl(
    private val api: AuthApi
): AuthRepository {
    override suspend fun auth(login: String, password: String): Resource<AuthInfo> {
        return try {
            Resource.Success(
                data = api.authUser(
                    login = login,
                    password = password
                ).toAuthInfo()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}