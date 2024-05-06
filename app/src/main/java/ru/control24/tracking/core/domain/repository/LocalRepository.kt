package ru.control24.tracking.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.control24.tracking.core.data.local.ObjectsInfoEntity
import ru.control24.tracking.core.data.local.UsersEntity

interface LocalRepository {
    suspend fun insertUser(user: UsersEntity)

    suspend fun deleteUser(user: UsersEntity)

    fun getActiveUser(): Flow<UsersEntity>

    fun getCurrentUserObjects(activeUser: String): Flow<List<ObjectsInfoEntity>>
}