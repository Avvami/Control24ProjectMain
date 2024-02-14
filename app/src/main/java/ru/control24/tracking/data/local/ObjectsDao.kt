package ru.control24.tracking.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ObjectsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackingObjects(trackingObjects: List<ObjectsInfoEntity>)
    @Update
    suspend fun updateTrackingObjects(trackingObjects: List<ObjectsInfoEntity>)
    @Query("DELETE FROM objectsinfoentity")
    suspend fun clearTrackingObjects()
}