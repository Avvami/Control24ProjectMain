package ru.control24.tracking.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Upsert
    suspend fun upsertUser(user: UsersEntity)

    @Delete
    suspend fun deleteUser(user: UsersEntity)

    @Query("SELECT * FROM usersentity WHERE active = 1")
    fun getActiveUser(): Flow<UsersEntity>

    @Query("UPDATE usersentity SET active = 0 WHERE active = 1")
    suspend fun deactivateCurrentUser()

    @Query("UPDATE usersentity SET active = 1 WHERE username = :username")
    suspend fun activateUser(username: String)
}