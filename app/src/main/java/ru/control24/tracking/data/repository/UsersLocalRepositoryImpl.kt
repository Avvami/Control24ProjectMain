package ru.control24.tracking.data.repository

import kotlinx.coroutines.flow.Flow
import ru.control24.tracking.data.local.ObjectsInfoEntity
import ru.control24.tracking.data.local.UsersDao
import ru.control24.tracking.data.local.UsersEntity
import ru.control24.tracking.domain.repository.UsersLocalRepository

class UsersLocalRepositoryImpl(
    private val usersDao: UsersDao
): UsersLocalRepository {
    override suspend fun insertUser(user: UsersEntity) = usersDao.insertUser(user)

    override suspend fun deleteUser(user: UsersEntity) = usersDao.deleteUser(user)

    override fun getActiveUser(): Flow<UsersEntity> = usersDao.getActiveUser()

    override fun loadCurrentUserObjects(activeUser: String): Flow<List<ObjectsInfoEntity>> =
        usersDao.loadCurrentUserObjects(activeUser)
}