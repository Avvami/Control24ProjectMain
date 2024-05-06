package ru.control24.tracking.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.control24.tracking.core.data.local.ObjectsInfoEntity
import ru.control24.tracking.core.data.local.UsersDao
import ru.control24.tracking.core.data.local.UsersEntity
import ru.control24.tracking.core.domain.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val usersDao: UsersDao
): LocalRepository {
    override suspend fun insertUser(user: UsersEntity) = usersDao.insertUser(user)

    override suspend fun deleteUser(user: UsersEntity) = usersDao.deleteUser(user)

    override fun getActiveUser(): Flow<UsersEntity> = usersDao.getActiveUser()

    override fun getCurrentUserObjects(activeUser: String): Flow<List<ObjectsInfoEntity>> =
        usersDao.getCurrentUserObjects(activeUser)
}