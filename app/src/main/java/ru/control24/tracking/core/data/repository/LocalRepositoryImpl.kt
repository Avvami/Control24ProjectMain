package ru.control24.tracking.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.control24.tracking.core.data.local.ObjectsDao
import ru.control24.tracking.core.data.local.ObjectsInfoEntity
import ru.control24.tracking.core.data.local.UsersDao
import ru.control24.tracking.core.data.local.UsersEntity
import ru.control24.tracking.core.domain.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val usersDao: UsersDao,
    private val objectsDao: ObjectsDao
): LocalRepository {
    override suspend fun upsertUser(user: UsersEntity) = usersDao.upsertUser(user)

    override suspend fun deleteUser(user: UsersEntity) = usersDao.deleteUser(user)

    override fun getActiveUser(): Flow<UsersEntity?> = usersDao.getActiveUser()

    override fun getCurrentUserObjects(username: String): Flow<List<ObjectsInfoEntity>> =
        objectsDao.getCurrentUserObjects(username)
}