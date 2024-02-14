package ru.control24.tracking.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UsersEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val active: Boolean = false
)