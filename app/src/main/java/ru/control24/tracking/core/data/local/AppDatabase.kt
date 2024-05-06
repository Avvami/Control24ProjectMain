package ru.control24.tracking.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ObjectsInfoEntity::class, UsersEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getObjectsDao(): ObjectsDao
    abstract fun getUsersDao(): UsersDao
}