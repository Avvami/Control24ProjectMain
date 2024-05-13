package ru.control24.tracking.core.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectsDao {
    @Upsert
    suspend fun upsertTrackingObjects(trackingObjects: List<ObjectsInfoEntity>)

    @Query(
        "SELECT * FROM objectsinfoentity " +
                "JOIN usersentity ON usersentity.username == objectsinfoentity.username " +
                "WHERE usersentity.active = 1 AND usersentity.username = :username"
    )
    fun getCurrentUserObjects(username: String): Flow<List<ObjectsInfoEntity>>

    @Query("DELETE FROM objectsinfoentity WHERE objectsinfoentity.username = :username")
    suspend fun clearTrackingObjects(username: String)
}