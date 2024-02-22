package ru.control24.tracking.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UsersEntity)

    @Delete
    suspend fun deleteUser(user: UsersEntity)

    @Query(
        "SELECT * FROM objectsinfoentity " +
                "JOIN usersentity ON usersentity.username == objectsinfoentity.username " +
                "WHERE usersentity.active = 1 AND usersentity.username = :currentUser"
    )
    fun loadCurrentUserObjects(currentUser: String): Flow<List<ObjectsInfoEntity>>

    @Query("SELECT * FROM usersentity WHERE active = 1")
    fun getActiveUser(): Flow<UsersEntity>

    @Query("UPDATE usersentity SET active = 0 WHERE active = 1")
    suspend fun deactivateCurrentUser()

    @Query("UPDATE usersentity SET active = 1 WHERE username = :username")
    suspend fun activateUser(username: String)
}