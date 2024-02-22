package ru.control24.tracking.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.control24.tracking.data.local.ObjectsInfoEntity
import ru.control24.tracking.data.local.UsersEntity

interface UsersLocalRepository {
    suspend fun insertUser(user: UsersEntity)

    suspend fun deleteUser(user: UsersEntity)

    fun getActiveUser(): Flow<UsersEntity>

    fun loadCurrentUserObjects(activeUser: String): Flow<List<ObjectsInfoEntity>>
}