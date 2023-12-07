package ru.control24.tracking.domain.repository

import ru.control24.tracking.domain.auth.AuthInfo
import ru.control24.tracking.domain.util.Resource

interface AuthRepository {
    suspend fun auth(login: String, password: String): Resource<AuthInfo>
}